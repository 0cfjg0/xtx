package com.itheima.xiaotuxian.service.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.KeywordStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsKeyword;
import com.itheima.xiaotuxian.entity.goods.GoodsKeywordRelation;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.goods.GoodsKeywordMapper;
import com.itheima.xiaotuxian.mapper.goods.GoodsKeywordRelationMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.goods.GoodsKeywordService;
import com.itheima.xiaotuxian.service.mq.producer.KeywordProducer;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.keyword.*;
import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:25 下午
 * @Description:
 */
@Service
public class GoodsKeywordServiceImpl extends ServiceImpl<GoodsKeywordMapper, GoodsKeyword> implements GoodsKeywordService {
    @Autowired
    private GoodsKeywordRelationMapper keywordRelationMapper;
    @Autowired
    private KeywordProducer keywordProducer;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private GoodsBrandService brandService;

    @Override
    public Boolean save(KeywordSaveVo saveVo, String opUser) {
        var keyword = BeanUtil.toBean(saveVo, GoodsKeyword.class);
        Optional.ofNullable(keyword.getId()).ifPresentOrElse(id -> Optional.ofNullable(getById(id))
                        .ifPresentOrElse(source -> Optional.ofNullable(saveVo.getTitle()).ifPresent(titles -> titles.forEach(title -> keywordDuplicate(title, id)))
                                , () -> {
                                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                })
                , () -> {
                    Optional.ofNullable(saveVo.getTitle()).ifPresent(titles -> titles.forEach(title -> keywordDuplicate(title, null)));
                    keyword.setCreator(opUser);
                }
        );
        //处理关键字
        Optional.ofNullable(saveVo.getTitle())
                .filter(CollUtil::isNotEmpty)
                .ifPresent(titles -> keyword.setTitle(String.join(",", titles)));
        //处理联想词
        Optional.ofNullable(saveVo.getAssociateWords())
                .filter(CollUtil::isNotEmpty)
                .ifPresent(associateWords -> keyword.setAssociateWords(String.join(",", associateWords)));
        //保存信息
        saveOrUpdate(keyword);
        // 处理关联关系
        Optional.ofNullable(saveVo.getRelations()).filter(CollUtil::isNotEmpty).ifPresent(relations ->
                relations.forEach(relationVo -> {
                    String relationKey = Optional.ofNullable(relationVo.getRelationKey()).filter(StrUtil::isNotEmpty).orElse(IdUtil.fastUUID());
                    keywordRelationMapper.delete(Wrappers.<GoodsKeywordRelation>lambdaQuery().eq(GoodsKeywordRelation::getRelationKey, relationKey));
                    var keywordsRelations = new ArrayList<GoodsKeywordRelation>();
                    //处理后台类目关联
                    Optional.ofNullable(relationVo.getBackends()).filter(CollUtil::isNotEmpty).ifPresent(backendIds ->
                            backendIds.forEach(id ->
                                    keywordsRelations.add(this.buildRelationEntity(keyword.getId(), relationKey, id, KeywordStatic.RELATION_TYPE_BACKEND))));
                    //处理销售属性组
                    Optional.ofNullable(relationVo.getPropertyGroups()).filter(CollUtil::isNotEmpty).ifPresent(propertyGroupIds ->
                            propertyGroupIds.forEach(id ->
                                    keywordsRelations.add(this.buildRelationEntity(keyword.getId(), relationKey, id, KeywordStatic.RELATION_TYPE_SALE_PROPERTY_GROUP))));
                    //处理品牌
                    Optional.ofNullable(relationVo.getBrands()).filter(CollUtil::isNotEmpty).ifPresent(brandSimpleIds ->
                            brandSimpleIds.forEach(id ->
                                    keywordsRelations.add(this.buildRelationEntity(keyword.getId(), relationKey, id, KeywordStatic.RELATION_TYPE_BRAND))));
                    keywordsRelations.forEach(keywordRelationMapper::insert);
                })
        );
        //发送 操作信息
        keywordProducer.sendOperator(keyword.getId(), KeywordStatic.OP_TYPE_SAVE);
        return true;
    }

    @Override
    public Boolean batchDelete(List<String> ids) {
        removeByIds(ids);
        ids.forEach(id -> keywordProducer.sendOperator(id, KeywordStatic.OP_TYPE_DELETE));
        return true;
    }

    @Override
    public Page<KeywordVo> findByPage(KeywordPageQueryVo queryVo) {
        var page = new Page<GoodsKeyword>(queryVo.getPage() == null ? 1 : queryVo.getPage(), queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
        page.addOrder(OrderItem.desc("create_time"));
        var dataPage = page(page, Wrappers
                .<GoodsKeyword>query()
                .like(StrUtil.isNotEmpty(queryVo.getTitle()), "title", "%" + queryVo.getTitle() + "%")
                .orderBy(StrUtil.isNotEmpty(queryVo.getSortName()) || StrUtil.isNotEmpty(queryVo.getSortMethod())
                        , StrUtil.isNotEmpty(queryVo.getSortMethod()) && "asc".equals(queryVo.getSortMethod())
                        , StrUtil.isNotEmpty(queryVo.getSortName()) ? queryVo.getSortName() : "createTime"
                )
        );
        var datas = dataPage.getRecords();
        var resultPage = new Page<KeywordVo>(dataPage.getCurrent(), dataPage.getSize(), dataPage.getTotal());
        var records = new ArrayList<KeywordVo>();
        datas.forEach(keyword -> {
            var keywordVo = BeanUtil.toBean(keyword, KeywordVo.class);
            //处理关键词
            keywordVo.setTitle(this.getListByString(keyword.getTitle()));
            //处理联想词
            keywordVo.setAssociateWords(this.getListByString(keyword.getAssociateWords()));
            //处理关联信息
            keywordVo.setRelationInfo(getRelationInfo(keyword.getId()));
            records.add(keywordVo);
        });
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public KeywordVo findDetailById(String id) {
        var keyword = Optional.ofNullable(getById(id))
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                });
        var keywordVo = BeanUtil.toBean(keyword, KeywordVo.class);
        //处理关键词
        keywordVo.setTitle(this.getListByString(keyword.getTitle()));
        //处理联想词
        keywordVo.setAssociateWords(this.getListByString(keyword.getAssociateWords()));
        //处理关联信息
        Optional.ofNullable(keywordRelationMapper.selectList(Wrappers
                .<GoodsKeywordRelation>lambdaQuery()
                .eq(GoodsKeywordRelation::getKeywordId, keyword.getId()))
        ).ifPresentOrElse(relationEntities -> {
            var relationVos = new ArrayList<KeywordRelationVo>();
            var allRelationMap = relationEntities.stream().collect(Collectors.groupingBy(GoodsKeywordRelation::getRelationKey));
            allRelationMap.forEach((key, value) -> {
                var relationVo = new KeywordRelationVo();
                relationVo.setRelationKey(key);
                var relationMap = value.stream().collect(Collectors.groupingBy(GoodsKeywordRelation::getObjectType));
                // 处理后台分类关联
                Optional.ofNullable(relationMap.get(KeywordStatic.RELATION_TYPE_BACKEND)).filter(CollUtil::isNotEmpty).ifPresentOrElse(relations -> {
                    var backends = new ArrayList<BackendSimpleVo>();
                    relations.forEach(relation ->
                            Optional.ofNullable(backendService.getById(relation.getObjectId())).ifPresent(backend ->
                                    backends.add(BeanUtil.toBean(backend, BackendSimpleVo.class))));
                    relationVo.setBackends(backends);
                }, () -> relationVo.setBackends(new ArrayList<>()));
                // 处理销售属性组关联
                Optional.ofNullable(relationMap.get(FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP)).filter(CollUtil::isNotEmpty).ifPresentOrElse(relations -> {
                    var propertyGroups = new ArrayList<PropertyGroupSimpleVo>();
                    relations.forEach(relation ->
                            Optional.ofNullable(propertyGroupService.getById(relation.getObjectId())).ifPresent(propertyGroup ->
                                    propertyGroups.add(BeanUtil.toBean(propertyGroup, PropertyGroupSimpleVo.class))));
                    relationVo.setPropertyGroups(propertyGroups);
                }, () -> relationVo.setPropertyGroups(new ArrayList<>()));
                // 处理品牌关联
                Optional.ofNullable(relationMap.get(FrontStatic.RELATION_TYPE_BRAND)).filter(CollUtil::isNotEmpty).ifPresentOrElse(relations -> {
                    var brands = new ArrayList<BrandSimpleVo>();
                    relations.forEach(relation ->
                            Optional.ofNullable(brandService.getById(relation.getObjectId())).ifPresent(brand ->
                                    brands.add(BeanUtil.toBean(brand, BrandSimpleVo.class))));
                    relationVo.setBrands(brands);
                }, () -> relationVo.setBrands(new ArrayList<>()));
                relationVos.add(relationVo);
            });
            keywordVo.setRelations(relationVos);
        }, () -> keywordVo.setRelations(new ArrayList<>()));
        return keywordVo;
    }

    @Override
    public List<GoodsKeyword> findAllByOr(KeywordQueryVo queryVo) {
        //获取关键字id
        var keywordIds = keywordRelationMapper.selectList(Wrappers
                .<GoodsKeywordRelation>lambdaQuery()
                .or(StrUtil.isNotEmpty(queryVo.getId()), wrapper -> wrapper.eq(GoodsKeywordRelation::getKeywordId, queryVo.getId()))
                .or(CollUtil.isNotEmpty(queryVo.getIds()), wrapper -> wrapper.in(GoodsKeywordRelation::getKeywordId, queryVo.getIds()))
                .or(StrUtil.isNotEmpty(queryVo.getBackendId())
                        , wrapper -> wrapper.eq(GoodsKeywordRelation::getObjectId, queryVo.getBackendId()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_BACKEND))
                .or(CollUtil.isNotEmpty(queryVo.getBackendIds())
                        , wrapper -> wrapper.in(GoodsKeywordRelation::getObjectId, queryVo.getBackendIds()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_BACKEND))
                .or(StrUtil.isNotEmpty(queryVo.getBrandId())
                        , wrapper -> wrapper.eq(GoodsKeywordRelation::getObjectId, queryVo.getBrandId()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_BRAND))
                .or(CollUtil.isNotEmpty(queryVo.getBackendIds())
                        , wrapper -> wrapper.in(GoodsKeywordRelation::getObjectId, queryVo.getBrandIds()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_BRAND))
                .or(StrUtil.isNotEmpty(queryVo.getPropertyGroupId())
                        , wrapper -> wrapper.eq(GoodsKeywordRelation::getObjectId, queryVo.getPropertyGroupId()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_SALE_PROPERTY_GROUP))
                .or(CollUtil.isNotEmpty(queryVo.getBackendIds())
                        , wrapper -> wrapper.in(GoodsKeywordRelation::getObjectId, queryVo.getPropertyGroupIds()).eq(GoodsKeywordRelation::getObjectType, KeywordStatic.RELATION_TYPE_SALE_PROPERTY_GROUP))
        ).stream().map(GoodsKeywordRelation::getKeywordId).distinct().collect(Collectors.toList());
        var results = new ArrayList<GoodsKeyword>();
        if (CollUtil.isNotEmpty(keywordIds)) {
            results.addAll(lambdaQuery().in(GoodsKeyword::getId, keywordIds).list());
        }
        return results;
    }

    /**
     * 构建关联实体
     *
     * @param keywordId   关键字id
     * @param relationKey 关联标识
     * @param objectId    对象id
     * @param objectType  对象类型
     * @return 关联实体
     */
    private GoodsKeywordRelation buildRelationEntity(String keywordId, String relationKey, String objectId, Integer objectType) {
        var keywordRelation = new GoodsKeywordRelation();
        keywordRelation.setKeywordId(keywordId);
        keywordRelation.setRelationKey(relationKey);
        keywordRelation.setObjectId(objectId);
        keywordRelation.setObjectType(objectType);
        return keywordRelation;
    }

    /**
     * 检查关键字是否重复
     *
     * @param name  关键字
     * @param curId 当前关键字id
     */
    private void keywordDuplicate(String name, String curId) {
        Stream.of(
                count(Wrappers
                        .<GoodsKeyword>lambdaQuery()
                        .ne(StrUtil.isNotEmpty(curId), GoodsKeyword::getId, curId)
                        .like(GoodsKeyword::getTitle, name)
                )
        ).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
        });
    }

    /**
     * 获取关联信息
     *
     * @param id 关键字Id
     * @return 关联信息
     */
    private String getRelationInfo(String id) {
        var result = new AtomicReference<String>();
        Optional.ofNullable(keywordRelationMapper.selectOne(Wrappers
                .<GoodsKeywordRelation>lambdaQuery()
                .eq(GoodsKeywordRelation::getKeywordId, id).last(" LIMIT 1"))
        ).ifPresent(relation -> {
            var count = keywordRelationMapper.selectCount(Wrappers.<GoodsKeywordRelation>lambdaQuery().eq(GoodsKeywordRelation::getKeywordId, id));
            var name = new AtomicReference<String>();
            Stream.of(relation.getObjectType()).filter(objectType -> objectType == KeywordStatic.RELATION_TYPE_BACKEND).forEach(objectType ->
                    Optional.ofNullable(backendService.getById(relation.getObjectId())).ifPresent(backend -> name.set(backend.getName())));
            Stream.of(relation.getObjectType()).filter(objectType -> objectType == KeywordStatic.RELATION_TYPE_SALE_PROPERTY_GROUP).forEach(objectType ->
                    Optional.ofNullable(propertyGroupService.getById(relation.getObjectId())).ifPresent(propertyGroup -> name.set(propertyGroup.getName())));
            Stream.of(relation.getObjectType()).filter(objectType -> objectType == KeywordStatic.RELATION_TYPE_BRAND).forEach(objectType ->
                    Optional.ofNullable(brandService.getById(relation.getObjectId())).ifPresent(brand -> name.set(brand.getName())));
            result.set(String.format("%s等%s个关联信息", name.get(), count));
        });
        return result.get();
    }

    /**
     * 将字符串转为List
     *
     * @param strData 字符串
     * @return list
     */
    private List<String> getListByString(String strData) {
        var results = new ArrayList<String>();
        Optional.ofNullable(strData).filter(StrUtil::isNotEmpty).ifPresent(aws ->
                results.addAll(Arrays.stream(aws.split(",")).map(String::trim).collect(Collectors.toList())));
        return results;
    }
}
