package com.itheima.xiaotuxian.service.classification.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.BackendStatic;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.entity.classification.ClassificationFrontRelation;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendMapper;
import com.itheima.xiaotuxian.mapper.classification.ClassificationFrontMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontRelationService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.mq.producer.FrontProducer;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleRelationVo;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.classification.FrontDetailNewVo;
import com.itheima.xiaotuxian.vo.classification.FrontPageQueryVo;
import com.itheima.xiaotuxian.vo.classification.FrontRelationSaveVo;
import com.itheima.xiaotuxian.vo.classification.FrontRelationVo;
import com.itheima.xiaotuxian.vo.classification.FrontSaveNewVo;
import com.itheima.xiaotuxian.vo.classification.FrontSaveVo;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleRelationVo;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.classification.FrontVo;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itheima.xiaotuxian.constant.statics.FrontStatic.MAX_LAYER;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:15 下午
 * @Description:
 */

@Slf4j
@Service
public class ClassificationFrontServiceImpl extends ServiceImpl<ClassificationFrontMapper, ClassificationFront>
        implements ClassificationFrontService {
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private FrontProducer frontProducer;
    @Autowired
    private ClassificationFrontRelationService frontRelationService;
    @Value("${picture.head.resize:?quality=95&imageView}")
    private String headResize;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ClassificationFrontService frontService;

    @Transactional
    @Override
    public Boolean saveFront(FrontSaveNewVo saveNewVo, String opUser) {

        FrontSaveVo saveVo = BeanUtil.copyProperties(saveNewVo,FrontSaveVo.class);

        List<BackendSimpleRelationVo> relations = saveNewVo.getRelations();
        List<FrontRelationSaveVo> frontRelationSaveVos = new ArrayList<>();
        FrontRelationSaveVo frontRelationSaveVo =new FrontRelationSaveVo();
        List<String> backends = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++) {
            BackendSimpleRelationVo relationVo =relations.get(i);
            backends.add(relationVo.getId());
        }
        frontRelationSaveVo.setBackends(backends);
        // 根据前台类目id，查询是否存在关联关系
        Optional.ofNullable(saveNewVo.getId())
                .ifPresent(id->{
                    List<ClassificationFrontRelation> frontRelations = this.frontRelationService.findRelationByFrontId(id);
                    if(CollectionUtils.isNotEmpty(frontRelations)){
                        Set<String> keysSet = frontRelations.stream().map(relation->relation.getRelationKey()).collect(Collectors.toSet());
                        if(keysSet.size() != 1){
                            log.error("保存前台类目失败，入参错误，请检查入参信息");
                            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
                        }else{
                            String  relationKey = frontRelations.get(0).getRelationKey();
                            frontRelationSaveVo.setRelationKey(relationKey);
                        }
                    }
                }
        );

        frontRelationSaveVos.add(frontRelationSaveVo);
        saveVo.setRelations(frontRelationSaveVos);
        saveVo.setPicture(saveNewVo.getPicture());
        saveVo.setParent(saveNewVo.getParent());
        return  this.saveFront(saveVo,opUser);
    }
    @Transactional
    @Override
    public Boolean saveFront(FrontSaveVo saveVo, String opUser) {
        // 处理主体信息
        var front = BeanUtil.toBean(saveVo, ClassificationFront.class);
        Optional.ofNullable(front.getId()).ifPresentOrElse(id -> Optional.ofNullable(getById(id))
                .ifPresentOrElse(source -> frontNameDuplicate(saveVo, source.getId()), () -> {
                    log.error("front:" + JSON.toJSONString(front));
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                }), () -> {
            if (null == saveVo.getPicture() ||
                    StringUtils.isBlank(saveVo.getPicture().getId())) {
                //增加默认图片复制
                PictureSimpleVo vo = new PictureSimpleVo();
                vo.setId("1379257816165912578");
                saveVo.setPicture(vo);
//                                             throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
            }
            frontNameDuplicate(saveVo, null);
            front.setCreator(opUser);
        });
        // 处理父级类目信息
        Optional.ofNullable(saveVo.getParent())
                .map(FrontSimpleVo::getId)
                .ifPresent(pid -> {
                    var parent = getById(pid);
                    Optional.ofNullable(parent).ifPresent(p -> {
                        front.setPid(p.getId());
                        Stream.of(p.getLayer()).filter(layer -> layer == 1)
                                .forEach(layer -> front.setRootId(p.getId()));
                        Stream.of(p.getLayer()).filter(layer -> layer == 2)
                                .forEach(layer -> front.setRootId(p.getRootId()));
                    });
                    Optional.ofNullable(parent).map(ClassificationFront::getLayer)
                            .filter(layer -> layer >= BackendStatic.MAX_LAYER)
                            .ifPresent(layer -> {
                                // 前台分类层数最大3级
                                throw new BusinessException(
                                        ErrorMessageEnum.CLASSIFICATION_FRONT_MAX_LAYER);
                            });
                    Optional.ofNullable(parent).map(ClassificationFront::getLayer)
                            .ifPresentOrElse(layer -> front.setLayer(layer + 1),
                                    () -> front.setLayer(1));
                });
        // 处理排序信息
        Optional.ofNullable(front.getId()).ifPresentOrElse(id -> Optional.ofNullable(saveVo.getSort())
                        .filter(sort -> sort > 0)
                        .ifPresent(front::setSort),
                () -> {
                    var pid = StrUtil.isEmpty(front.getPid()) ? "0" : front.getPid();
                    var sort = this.baseMapper.getMaxSort(pid);
                    front.setSort(sort == null ? 1 : sort + 1);
                });
        // 处理图片信息
        Optional.ofNullable(saveVo.getPicture()).map(PictureSimpleVo::getId).filter(StrUtil::isNotEmpty)
                .flatMap(pictureId -> Optional.ofNullable(pictureService.findById(pictureId)))
                .ifPresent(picture -> front.setPictureId(picture.getId()));
        // 保存信息
        saveOrUpdate(front);
        // 保存关联信息
        Optional.ofNullable(saveVo.getRelations())
                .filter(CollUtil::isNotEmpty)
                .ifPresent(relations -> relations.forEach(relationVo -> {
                        String relationKey = Optional.ofNullable(relationVo.getRelationKey())
                                .filter(StrUtil::isNotEmpty)
                                .orElse(IdUtil.fastUUID());
                        frontRelationService.remove(Wrappers.<ClassificationFrontRelation>lambdaQuery()
                                .eq(ClassificationFrontRelation::getRelationKey, relationKey));
                        var frontRelations = new ArrayList<ClassificationFrontRelation>();
                        // 处理后台类目关联
                        Optional.ofNullable(relationVo.getBackends())
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(backendSimpleVos -> backendSimpleVos.stream()
                                        .filter(StrUtil::isNotEmpty)
                                        .forEach(id -> frontRelations
                                                .add(this.buildRelationEntity(
                                                        front.getId(),
                                                        relationKey, id,
                                                        FrontStatic.RELATION_TYPE_BACKEND))));
                        // 处理销售属性组
                        Optional.ofNullable(relationVo.getPropertyGroups())
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(propertyGroupSimpleVos -> propertyGroupSimpleVos
                                        .stream()
                                        .filter(StrUtil::isNotEmpty)
                                        .forEach(id -> frontRelations
                                                .add(this.buildRelationEntity(
                                                        front.getId(),
                                                        relationKey, id,
                                                        FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP))));
                        // 处理品牌
                        Optional.ofNullable(relationVo.getBrands())
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(brandSimpleVos -> brandSimpleVos
                                        .forEach(id -> frontRelations
                                                .add(this.buildRelationEntity(
                                                        front.getId(),
                                                        relationKey, id,
                                                        FrontStatic.RELATION_TYPE_BRAND))));
                        frontRelations.forEach(frontRelationService::save);
                        frontProducer.sendOperator(relationKey, FrontStatic.OP_TYPE_RELATION);
                }));
        return true;
    }

    @Transactional
    @Override
    public Boolean batchDelete(List<String> ids) {
//        // 验证子级是否在使用
//        ids.forEach(id -> Stream.of(count(Wrappers.<ClassificationFront>lambdaQuery().eq(ClassificationFront::getPid, id)
//                .eq(ClassificationFront::getState, 0)))
//                .filter(count -> count > 0)
//                .forEach(count -> {
//                    log.error("id" + id);
//                    throw new BusinessException(ErrorMessageEnum.OBJECT_USED);
//                }));
//        //验证当前级别是否在被使用
//        ids.forEach(id -> Stream.of(count(Wrappers.<ClassificationFront>lambdaQuery().eq(ClassificationFront::getId, id)
//                .eq(ClassificationFront::getState, 0)))
//                .filter(count -> count > 0)
//                .forEach(count -> {
//                    log.error("id" + id);
//                    throw new BusinessException(ErrorMessageEnum.OBJECT_USED);
//                }));
        // 全部集合
        var allFronts = new ArrayList<ClassificationFront>();
        ids.forEach(id ->{
            if (allFronts.stream()
                    .filter(item -> item.getId()
                            .equals(id))
                    .findAny()
                    .isPresent()) {
                //存在 则继续循环
            } else {
                var childrens = this.getAllChildren(id);
                var curFront = this.getById(id);
                allFronts.addAll(childrens);
                allFronts.add(curFront);
            }
        });
        // 检查是否当前类目及所有子类目都为禁用状态，禁用状态的数据才可以删除
//        var checkResult = allFronts.stream().filter(item->   item.getState().equals(0)).findAny().isPresent();
        var checkResult = allFronts.stream().filter((item)->{
            return   item.getState().equals(0);
        }).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(checkResult)){
            log.error("对象正在被占用:"+JSON.toJSONString(checkResult));
            throw  new BusinessException(ErrorMessageEnum.OBJECT_USED);
        }
        // 传入的参数需要包含所有的子id,反向验证
        allFronts.stream().forEach(item->{
            boolean flag = ids.stream().filter(id->item.getId().equals(id)).findAny().isPresent();
            if(!flag){
                throw  new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
            }
        });

        removeByIds(ids);
        frontRelationService.remove(Wrappers.<ClassificationFrontRelation>lambdaQuery()
                .in(ClassificationFrontRelation::getFrontId, ids));
        return true;
    }
    @Override
    public FrontDetailNewVo findDetailNewById(String id) {
        var frontEntity = Optional.ofNullable(getById(id))
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                });
        var frontDetailNewVo = BeanUtil.toBean(frontEntity, FrontDetailNewVo.class);
        // 处理图片信息
        Optional.ofNullable(frontEntity.getPictureId())
                .filter(StrUtil::isNotEmpty)
                .flatMap(pictureId -> Optional.ofNullable(pictureService.findById(pictureId)))
                .ifPresent(picture -> frontDetailNewVo
                        .setPicture(BeanUtil.toBean(picture, PictureSimpleVo.class)));
        // 处理父类信息
        frontDetailNewVo.setParent(this.getFullParent4Relation(frontEntity.getPid()));
        // 处理关联信息
        var relationVos = new ArrayList<BackendSimpleRelationVo>();

        Optional.ofNullable(frontRelationService.list(Wrappers
                .<ClassificationFrontRelation>lambdaQuery()
                .eq(ClassificationFrontRelation::getFrontId, frontDetailNewVo.getId())))
                .ifPresent(relationEntities -> {
                    var allRelationMap = relationEntities.stream()
                            .collect(Collectors.groupingBy(
                                    ClassificationFrontRelation::getRelationKey));
                    allRelationMap.forEach((key, value) -> {
                        var relationMap = value.stream()
                                .collect(Collectors.groupingBy(
                                        ClassificationFrontRelation::getObjectType));
                        // 处理后台分类关联
                        Optional.ofNullable(relationMap.get(FrontStatic.RELATION_TYPE_BACKEND))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(relations -> {
                                    var backends = new ArrayList<BackendSimpleVo>();
                                    relations.forEach(relation -> Optional
                                            .ofNullable(backendService.getById(relation.getObjectId()))
                                            .ifPresent(backend -> relationVos.add(BeanUtil.toBean( backend, BackendSimpleRelationVo.class))));
                                });
                    });
                });
        frontDetailNewVo.setRelations(relationVos);
        return frontDetailNewVo;
    }

    @Autowired
    private ClassificationBackendMapper backendMapper;


    @Override
    public List<FrontResultVo> getHead() {
        List<FrontResultVo> categoryParents = backendMapper.getCategoryParents();
        for (int i = 0; i < categoryParents.size(); i++) {
            FrontResultVo parent = categoryParents.get(i);
            String pid = parent.getId();
//拿到所有的二级类目
            List<FrontResultVo> son = backendMapper.getCategoryOneFindTwo(pid);
            parent.setChildren(son);
//拿到所有的某个1级类日下的商品(前9个)
            List<GoodsItemResultVo> goods = backendMapper.getCategoryGoodsForOneId(pid);
            String cnt1 = null;
            String cnt2 = null;
            List<GoodsItemResultVo> newgoods = new ArrayList<>();
            for (int j = 0; j < goods.size(); j++) {
                GoodsItemResultVo goodsOfOne = goods.get(j);
                cnt2 = goodsOfOne.getId();
                if (!cnt2.equals(cnt1)) {
                    cnt1 = cnt2;
                    newgoods.add(goodsOfOne);
                }
            }
            parent.setGoods(newgoods);
        }
        return categoryParents;
    }


    /**
     * 根据前台类目id,查询详细信息
     *
     * @param id 前台类目Id
     * @return
     */
    @Override
    public FrontVo findDetailById(String id) {
        var frontEntity = Optional.ofNullable(getById(id))
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                });
        var frontVo = BeanUtil.toBean(frontEntity, FrontVo.class);
        // 处理图片信息
        Optional.ofNullable(frontEntity.getPictureId())
                .filter(StrUtil::isNotEmpty)
                .flatMap(pictureId -> Optional.ofNullable(pictureService.findById(pictureId)))
                .ifPresent(picture -> frontVo
                        .setPicture(BeanUtil.toBean(picture, PictureSimpleVo.class)));
        // 处理父类信息
        frontVo.setParent(this.getFullParent(frontEntity.getPid()));
        // 处理关联信息
        Optional.ofNullable(frontRelationService.list(Wrappers
                .<ClassificationFrontRelation>lambdaQuery()
                .eq(ClassificationFrontRelation::getFrontId, frontVo.getId())))
                .ifPresent(relationEntities -> {
                    var relationVos = new ArrayList<FrontRelationVo>();
                    var allRelationMap = relationEntities.stream()
                            .collect(Collectors.groupingBy(
                                    ClassificationFrontRelation::getRelationKey));
                    allRelationMap.forEach((key, value) -> {
                        var relationVo = new FrontRelationVo();
                        relationVo.setRelationKey(key);
                        var relationMap = value.stream()
                                .collect(Collectors.groupingBy(
                                        ClassificationFrontRelation::getObjectType));
                        // 处理后台分类关联
                        Optional.ofNullable(relationMap.get(FrontStatic.RELATION_TYPE_BACKEND))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresentOrElse(relations -> {
                                    var backends = new ArrayList<BackendSimpleVo>();
                                    relations.forEach(relation -> Optional
                                            .ofNullable(backendService.getById(relation.getObjectId()))
                                            .ifPresent(backend -> backends.add(BeanUtil.toBean( backend, BackendSimpleVo.class))));
                                    relationVo.setBackends(backends);
                                }, () -> relationVo.setBrands(new ArrayList<>()));
                        // 处理销售属性组关联
                        Optional.ofNullable(relationMap
                                .get(FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresentOrElse(relations -> {
                                    var propertyGroups = new ArrayList<PropertyGroupSimpleVo>();
                                    relations.forEach(relation -> Optional
                                            .ofNullable(propertyGroupService
                                                    .getById(relation
                                                            .getObjectId()))
                                            .ifPresent(propertyGroup -> propertyGroups
                                                    .add(BeanUtil.toBean(
                                                            propertyGroup,
                                                            PropertyGroupSimpleVo.class))));
                                    relationVo.setPropertyGroups(propertyGroups);
                                }, () -> relationVo
                                        .setPropertyGroups(new ArrayList<>()));
                        // 处理品牌关联
                        Optional.ofNullable(relationMap.get(FrontStatic.RELATION_TYPE_BRAND))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresentOrElse(relations -> {
                                    var brands = new ArrayList<BrandSimpleVo>();
                                    relations.forEach(relation -> Optional
                                            .ofNullable(brandService
                                                    .getById(relation
                                                            .getObjectId()))
                                            .ifPresent(
                                                    brand -> brands.add(
                                                            BeanUtil.toBean(brand,
                                                                    BrandSimpleVo.class))));
                                    relationVo.setBrands(brands);
                                }, () -> relationVo.setBrands(new ArrayList<>()));
                        relationVos.add(relationVo);
                    });
                    frontVo.setRelations(relationVos);
                });
        return frontVo;
    }

    /**
     * @param id 前台分类id
     * @return
     */
    @Cacheable(value = "xiaotuxian",key = "methodName +':id:'+#root.args[0]")
    @Override
    public FrontSimpleVo findSimpleById(String id) {
        var frontEntity = Optional.ofNullable(getById(id))
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                });
        var resultVo = BeanUtil.toBean(frontEntity, FrontSimpleVo.class);
        Optional.ofNullable(frontEntity.getPid())
                .filter(pid -> StrUtil.isNotEmpty(pid) && !"0".equals(pid))
                .ifPresent(pid -> resultVo.setParent(getFullParent(pid)));
        return resultVo;
    }

    /**
     * 后台管理系统分页获取前台类目信息列表
     *
     * @param queryVo 查询条件
     * @return
     */
    @Override
    public Page<FrontVo> findByPage(FrontPageQueryVo queryVo) {
        var page = new Page<ClassificationFront>(queryVo.getPage() == null ? 1 : queryVo.getPage(),
                queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
        page.addOrder(OrderItem.desc("sort"));
        var dataPage = this.page(page, Wrappers.<ClassificationFront>lambdaQuery()
                .eq(ClassificationFront::getLayer, 1)
                .like(StrUtil.isNotEmpty(queryVo.getName()), ClassificationFront::getName,
                        "%" + queryVo.getName() + "%")
                .ge(StrUtil.isNotEmpty(queryVo.getStartDate()), ClassificationFront::getCreateTime,
                        queryVo.getStartDate() + " 00:00:00")
                .le(StrUtil.isNotEmpty(queryVo.getEndDate()), ClassificationFront::getCreateTime,
                        queryVo.getEndDate() + " 23:59:59"));
        var datas = dataPage.getRecords();
        var resultPage = new Page<FrontVo>(dataPage.getCurrent(), dataPage.getSize(), dataPage.getTotal());
        var records = fillFrontInfo(datas);
        records.forEach(record -> Optional
                .ofNullable(list(
                        Wrappers.<ClassificationFront>lambdaQuery()
                                .eq(ClassificationFront::getPid, record.getId())))
                .filter(CollUtil::isNotEmpty).ifPresentOrElse(subDatas -> {
                    var subRecords = fillFrontInfo(subDatas);

                    subRecords.forEach(subRecord -> Optional
                            .ofNullable(list(Wrappers.<ClassificationFront>lambdaQuery().eq(
                                    ClassificationFront::getPid,
                                    subRecord.getId())))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresentOrElse(
                                    subSubDatas -> subRecord.setChildren(
                                            fillFrontInfo(subSubDatas)),
                                    () -> subRecord.setChildren(
                                            new ArrayList<>())));
                    record.setChildren(subRecords);
                }, () -> record.setChildren(new ArrayList<>())));
        // 获取前台类目关联的商品数量
        this.setFrontGoodsCount(records);

        resultPage.setRecords(records);
        return resultPage;
    }

    private void setFrontGoodsCount(List<FrontVo> records) {

        // 如果是父级，就统计出子级关联的后台类目信息
        List<String> objList = new ArrayList<>();

        records.forEach(level1Record->{
            List<String> level1StrList = new ArrayList<>();

            // 先拿到子级关联的信息，再和当前类型的合并
            if(CollectionUtils.isNotEmpty(level1Record.getChildren())){
                List<String> level2StrList = new ArrayList<>();
                List<FrontVo> level2List = level1Record.getChildren();
                level2List.forEach(level2Record->{
                    List<String> level3StrList = new ArrayList<>();

                    if(CollectionUtils.isNotEmpty(level2Record.getChildren())) {
                        List<FrontVo> grandson = level2Record.getChildren();
                        grandson.forEach(level3Record->{
                            var grandsonId = level3Record.getId();
                            var grandSonRelations = frontRelationService.findRelationByFrontId(grandsonId);
                            List<String> idList = grandSonRelations.stream().map(e -> e.getObjectId()).collect(Collectors.toList());
                            Integer goodsCount = CollectionUtils.isEmpty(idList) ? 0 :  goodsSpuService.count(Wrappers.<GoodsSpu>lambdaQuery().in(GoodsSpu::getClassificationBackendId, idList));
                            level3Record.setGoodsCount(goodsCount);
                            level3StrList.addAll(idList);


                        });


                    }
                    var cid = level2Record.getId();
                    var grandSonRelations = frontRelationService.findRelationByFrontId(cid);
                    List<String> cidListd = grandSonRelations.stream().map(e -> e.getObjectId()).collect(Collectors.toList());
                    cidListd.addAll(level3StrList);
                    level2StrList.addAll(level3StrList);

                    Integer cgoodsCount = CollectionUtils.isEmpty(cidListd) ? 0 : goodsSpuService.count(Wrappers.<GoodsSpu>lambdaQuery().in(GoodsSpu::getClassificationBackendId, cidListd));

                    level2Record.setGoodsCount(cgoodsCount);
                    level1StrList.addAll(level2StrList);

                });
            }
            // 查询当前类型关联的信息ArrayList
            var relations = frontRelationService.findRelationByFrontId(level1Record.getId());
            List<String> idList = relations.stream().map(e -> e.getObjectId()).collect(Collectors.toList());
            idList.addAll(level1StrList);

            // 查询相关商品的数量
            Integer goodsCount =  CollectionUtils.isEmpty(idList) ? 0 : goodsSpuService.count(Wrappers.<GoodsSpu>lambdaQuery().in(GoodsSpu::getClassificationBackendId, idList));
            level1Record.setGoodsCount(goodsCount);

        });
    }

    /**
     * 根据parentId查询前台合法的类目信息
     *
     * @param parentId
     * @return
     */
     @Cacheable(value = "xiaotuxian",key = "methodName +':parentId:'+#root.args[0]")
    @Override
    public List<ClassificationFront> findAllValidFront(String parentId) {
        return list(Wrappers.<ClassificationFront>lambdaQuery()
                .eq(ClassificationFront::getPid, StrUtil.isEmpty(parentId) ? "0" : parentId)
                .eq(ClassificationFront::getState, FrontStatic.STATE_NORMAL));
    }

    /**
     * 后台管理系统获取前台类目列表
     *
     * @param datas
     * @return
     */
    private List<FrontVo> fillFrontInfo(List<ClassificationFront> datas) {
        var records = new ArrayList<FrontVo>();
        datas.forEach(data -> {
            var record = BeanUtil.toBean(data, FrontVo.class);
            // 处理图片信息
            Optional.ofNullable(data.getPictureId()).filter(StrUtil::isNotEmpty)
                    .flatMap(pictureId -> Optional.ofNullable(pictureService.findById(pictureId)))
                    .ifPresent(picture -> record
                            .setPicture(BeanUtil.toBean(picture, PictureSimpleVo.class)));
            // 处理父类信息
            record.setParent(this.getFullParent(data.getPid()));
            // 处理关联信息
            record.setRelationInfo(this.getRelationInfo(data.getId()));
            records.add(record);
        });
        records.sort((r1, r2) -> r2.getSort().compareTo(r1.getSort()));
        return records;
    }

    /**
     * 获取关联信息
     * 用于显示当前的前台类目id关联信息
     *
     * @param id 前台类目Id
     * @return 关联信息
     */
    private String getRelationInfo(String id) {
        var result = new AtomicReference<String>();
        Optional.ofNullable(frontRelationService.getOne(Wrappers
                .<ClassificationFrontRelation>lambdaQuery()
            .eq(ClassificationFrontRelation::getFrontId, id).last(" LIMIT 1")))
                .ifPresent(relation -> {
                    var count = frontRelationService.count(Wrappers
                            .<ClassificationFrontRelation>lambdaQuery()
                            .eq(ClassificationFrontRelation::getFrontId, id));
                    var name = new AtomicReference<String>();
                    Stream.of(relation.getObjectType())
                            .filter(objectType -> objectType == FrontStatic.RELATION_TYPE_BACKEND)
                            .forEach(objectType -> Optional
                                    .ofNullable(backendService.getById(
                                            relation.getObjectId()))
                                    .ifPresent(backend -> name
                                            .set(backend.getName())));
                    Stream.of(relation.getObjectType())
                            .filter(objectType -> objectType == FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP)
                            .forEach(objectType -> Optional
                                    .ofNullable(propertyGroupService.getById(
                                            relation.getObjectId()))
                                    .ifPresent(propertyGroup -> name
                                            .set(propertyGroup.getName())));
                    Stream.of(relation.getObjectType())
                            .filter(objectType -> objectType == FrontStatic.RELATION_TYPE_BRAND)
                            .forEach(objectType -> Optional
                                    .ofNullable(brandService.getById(
                                            relation.getObjectId()))
                                    .ifPresent(brand -> name.set(brand.getName())));
                    if (count == 1) {
                        result.set(String.format("%s共%s个关联信息", name.get(), count));
                    } else {
                        result.set(String.format("%s等%s个关联信息", name.get(), count));
                    }
                });
        return result.get();
    }

    /**
     * 构建关联实体
     *
     * @param frontId     前台分类id
     * @param relationKey 关联标识
     * @param objectId    对象id
     * @param objectType  对象类型
     * @return 关联实体
     */
    private ClassificationFrontRelation buildRelationEntity(String frontId, String relationKey, String objectId,
                                                            Integer objectType) {
        var frontRelation = new ClassificationFrontRelation();
        frontRelation.setFrontId(frontId);
        frontRelation.setRelationKey(relationKey);
        frontRelation.setObjectId(objectId);
        frontRelation.setObjectType(objectType);
        return frontRelation;
    }

    /**
     * 检查前台分类名称是否重复
     *
     * @param saveVo 前台分类信息
     * @param curId  当前前台分类Id
     */
    private void frontNameDuplicate(FrontSaveVo saveVo, String curId) {
        var queryWrapper = Wrappers.<ClassificationFront>lambdaQuery();
        queryWrapper.eq(ClassificationFront::getName, saveVo.getName());
        Optional.ofNullable(saveVo.getParent()).map(FrontSimpleVo::getId)
                .ifPresent(pid -> queryWrapper.eq(ClassificationFront::getPid, pid));
        Optional.ofNullable(curId).ifPresent(gid -> queryWrapper.ne(ClassificationFront::getId, curId));
        Stream.of(count(queryWrapper)).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
        });
    }

    /**
     * 获取全链路父级信息
     *
     * @param parentId 直接父级Id
     * @return 全链路父级信息
     */
    private FrontSimpleVo getFullParent(String parentId) {
        var resultVo = new AtomicReference<FrontSimpleVo>();
        Optional.ofNullable(parentId).filter(StrUtil::isNotEmpty).filter(pid -> !"0".equals(pid))
                .flatMap(pid -> Optional.ofNullable(getById(pid))).ifPresent(parent -> {
            var parentVo = BeanUtil.toBean(parent, FrontSimpleVo.class);
            Optional.ofNullable(parent.getPid()).filter(StrUtil::isNotEmpty)
                    .filter(ppid -> !"0".equals(ppid))
                    .flatMap(ppid -> Optional.ofNullable(getById(ppid)))
                    .ifPresent(pparent -> parentVo.setParent(
                            BeanUtil.toBean(pparent, FrontSimpleVo.class)));
            resultVo.set(parentVo);
        });
        return resultVo.get();
    }

    /**
     * 解决返回结果属性不一样的问题
     * @param parentId
     * @return
     */
    private FrontSimpleRelationVo getFullParent4Relation(String parentId) {
        var resultVo = new AtomicReference<FrontSimpleRelationVo>();
        Optional.ofNullable(parentId).filter(StrUtil::isNotEmpty).filter(pid -> !"0".equals(pid))
                .flatMap(pid -> Optional.ofNullable(getById(pid))).ifPresent(parent -> {
            var parentVo = BeanUtil.toBean(parent, FrontSimpleRelationVo.class);
            Optional.ofNullable(parent.getPid()).filter(StrUtil::isNotEmpty)
                    .filter(ppid -> !"0".equals(ppid))
                    .flatMap(ppid -> Optional.ofNullable(getById(ppid)))
                    .ifPresent(pparent -> parentVo.setParent(
                            BeanUtil.toBean(pparent, FrontSimpleRelationVo.class)));
            resultVo.set(parentVo);
        });
        return resultVo.get();
    }

    /**
     * 根据关联的条件（类目、属性、品牌），递归获得关联的frontId
     * 根据frontId，得到所有的相关的前台类目信息
     *
     * @param relationId
     * @return
     */
    @Override
    public List<FrontSimpleVo> findFrontSimplesByRelationId(String relationId, int type) {
        List<FrontSimpleVo> frontSimpleVos = new ArrayList<>();
        var fronts = new ArrayList<String>();
        // 提取其关联的前台类目
        frontRelationService.findAllByRelation(Collections.singletonList(relationId), type)
                .forEach(frontRelation -> {
                    fronts.add(frontRelation.getFrontId());
                });
        Optional.ofNullable(fronts).filter(CollUtil::isNotEmpty).ifPresent(f -> {
            recursive(f.get(0), frontSimpleVos);
        });
        return frontSimpleVos;
    }

    /**
     * 递归查找父级的前台类目
     *
     * @param frontId
     * @param frontSimpleVos
     */
    private void recursive(String frontId, List<FrontSimpleVo> frontSimpleVos) {
        var frontSimpleVo = findSimpleById(frontId);
        Optional.ofNullable(frontSimpleVo).ifPresent(frontSimpleVo1 -> {
            frontSimpleVos.add(frontSimpleVo);
            Optional.ofNullable(frontSimpleVo.getParent()).ifPresent(parent -> {
                recursive(frontSimpleVo.getParent().getId(), frontSimpleVos);
            });
        });
    }

    /**
     * 获取分类并且获取分类下关联的商品
     * 物品个数根据pageSize决定
     * 物品的图片信息根据showClient决定
     *
     * @return 分类集合
     */



    /**
     * 获取分类
     *
     * @return 分类集合
     */
    public List<FrontResultVo> findCategory() {
        return this.findAllValidFront("0")
                .stream().map(front -> {
                    var resultVo = BeanUtil.toBean(front, FrontResultVo.class);
                    // 处理图片
                    Optional.ofNullable(front.getPictureId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(pictureId -> resultVo.setPicture(
                                    pictureService.getPictureUrl(pictureId)));
                    return resultVo;
                }).collect(Collectors.toList());
    }



    /**
     * 递归获取子分类的信息
     * 用于获取子类信息
     *
     * @param pid
     * @return
     */
    private List<ClassificationFront> getChilrens(String pid) {
        List<ClassificationFront> backends = this.list(Wrappers.<ClassificationFront>lambdaQuery()
                .eq(StrUtil.isNotEmpty(pid), ClassificationFront::getPid, pid)
                .orderByAsc(ClassificationFront::getId));
        return backends;
    }

    /**
     * 递归获得子级的集合
     * 用于获取子类id集合
     *
     * @param pid
     * @return
     */
    private List<ClassificationFront> getAllChildren(String pid) {
        var allChildren = new ArrayList<ClassificationFront>();
        var myChild = this.getChilrens(pid);
        allChildren.addAll(myChild);
        Optional.ofNullable(myChild).ifPresent(items->{
            items.stream().forEach(item->{
                if(item.getLayer()<MAX_LAYER){
                    allChildren.addAll(this.getChilrens(item.getId()));
                    log.info("子集的子集:{}，id:{},allChildren:{}",item.getLayer(),item.getId(),JSON.toJSONString(allChildren));
                }
            });
        });
         return allChildren;
    }

}
