package com.itheima.xiaotuxian.service.classification.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.BackendStatic;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.PropertyStatic;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackend;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendBrand;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendPropertyGroup;
import com.itheima.xiaotuxian.entity.classification.ClassificationFrontRelation;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendBrandMapper;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendMapper;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendPropertyGroupMapper;
import com.itheima.xiaotuxian.mapper.classification.ClassificationFrontRelationMapper;
import com.itheima.xiaotuxian.mapper.goods.GoodsBrandMapper;
import com.itheima.xiaotuxian.mapper.property.PropertyGroupMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendBrandService;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontRelationService;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.vo.classification.*;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itheima.xiaotuxian.constant.statics.BackendStatic.MAX_LAYER;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:09 下午
 * @Description:
 */
@Service
public class ClassificationBackendServiceImpl extends ServiceImpl<ClassificationBackendMapper, ClassificationBackend>
        implements ClassificationBackendService {
    @Autowired
    private ClassificationBackendPropertyGroupMapper backendPropertyGroupMapper;
    @Autowired
    private ClassificationBackendBrandMapper backendBrandMapper;
    @Autowired
    private ClassificationFrontRelationMapper frontRelationMapper;
    @Autowired
    private GoodsBrandMapper goodsBrandMapper;
    @Autowired
    private PropertyGroupMapper propertyGroupMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private ClassificationBackendBrandService backendBrandService;
    @Autowired
    private ClassificationFrontRelationService frontRelationService;
    @Autowired
    private ClassificationBackendPropertyGroupServiceImpl backendPropertyGroupService;

    @Transactional
    @Override
    public String saveBackend(BackendSaveVo backendVo, String opUser) {
        // 保存类目主体信息
        var backend = BeanUtil.toBean(backendVo, ClassificationBackend.class);
        Optional.ofNullable(backend.getId()).ifPresentOrElse(id -> Optional.ofNullable(getById(id))
                .ifPresentOrElse(source -> backendNameDuplicate(backendVo, source.getId()), () -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                }), () -> {
                    backendNameDuplicate(backendVo, null);
                    backend.setCreator(opUser);
                });
        // 处理父级类目信息
        Optional.ofNullable(backendVo.getParent())
                .map(BackendSimpleVo::getId)
                .ifPresent(pid -> {
                    var parent = getById(pid);
                    Optional.ofNullable(parent).map(ClassificationBackend::getId).ifPresent(backend::setPid);
                    Optional.ofNullable(parent).map(ClassificationBackend::getLayer)
                            .filter(layer -> layer >= MAX_LAYER).ifPresent(layer -> {
                                // 后台分类层数最大3级
                                throw new BusinessException(ErrorMessageEnum.CLASSIFICATION_BACKEND_MAX_LAYER);
                            });
                    Optional.ofNullable(parent).map(ClassificationBackend::getLayer)
                            .ifPresentOrElse(layer -> backend.setLayer(layer + 1), () -> backend.setLayer(1));
                });
        // 保存信息
        saveOrUpdate(backend);
        // 保存属性组关联 -> 关键属性组 - 基础属性组 - 销售属性组 - 其他属性组
        var backendPropertyGroups = new ArrayList<ClassificationBackendPropertyGroup>();
        if (CollUtil.isNotEmpty(backendVo.getCrucialProperties())) {
            backendPropertyGroups.addAll(parseBackendPropertyGroupList(opUser, backend.getId(),
                    backendVo.getCrucialProperties(), PropertyStatic.TYPE_CRUCIAL));
        }
        if (CollUtil.isNotEmpty(backendVo.getBaseProperties())) {
            backendPropertyGroups.addAll(parseBackendPropertyGroupList(opUser, backend.getId(),
                    backendVo.getBaseProperties(), PropertyStatic.TYPE_BASE));
        }
        if (CollUtil.isNotEmpty(backendVo.getSaleProperties())) {
            backendPropertyGroups.addAll(parseBackendPropertyGroupList(opUser, backend.getId(),
                    backendVo.getSaleProperties(), PropertyStatic.TYPE_SALE));
        }
        if (CollUtil.isNotEmpty(backendVo.getExtraProperties())) {
            backendPropertyGroups.addAll(parseBackendPropertyGroupList(opUser, backend.getId(),
                    backendVo.getExtraProperties(), PropertyStatic.TYPE_EXTRA));
        }
        saveBackendPropertyGroupBatch(backend.getId(), backendPropertyGroups);
        // 保存品牌关联
        if (CollUtil.isNotEmpty(backendVo.getBrands())) {
            var backendBrands = new ArrayList<ClassificationBackendBrand>();
            backendBrands.addAll(backendVo.getBrands().stream().map(brandId -> {
                ClassificationBackendBrand backendBrand = new ClassificationBackendBrand();
                backendBrand.setBrandId(brandId);
                backendBrand.setClassificationBackendId(backend.getId());
                return backendBrand;
            }).collect(Collectors.toList()));
            saveBackendBrandBatch(backend.getId(), backendBrands);
        } else {
            // 删除原有的品牌关联
            saveBackendBrandBatch(backend.getId(), new ArrayList<ClassificationBackendBrand>());
        }
        return backend.getId();
    }

    @Override
    public Boolean saveBackendPropertyGroupBatch(String backendId,
            List<ClassificationBackendPropertyGroup> backendPropertyGroups) {
        if (StrUtil.isNotEmpty(backendId)) {
            backendPropertyGroupMapper.delete(new LambdaQueryWrapper<ClassificationBackendPropertyGroup>()
                    .eq(ClassificationBackendPropertyGroup::getClassificationBackendId, backendId));
        }
        if (CollUtil.isNotEmpty(backendPropertyGroups)) {
            backendPropertyGroups
                    .forEach(backendPropertyGroup -> backendPropertyGroupMapper.insert(backendPropertyGroup));
        }
        return true;
    }

    @Override
    public Boolean saveBackendBrandBatch(String backendId, List<ClassificationBackendBrand> backendBrands) {
        if (StrUtil.isNotEmpty(backendId)) {
            backendBrandMapper.delete(new LambdaQueryWrapper<ClassificationBackendBrand>()
                    .eq(ClassificationBackendBrand::getClassificationBackendId, backendId));
        }
        if (CollUtil.isNotEmpty(backendBrands)) {
            backendBrands.forEach(backendBrand -> backendBrandMapper.insert(backendBrand));
        }
        return true;
    }

    @Transactional
    @Override
    public Boolean batchDelete(List<String> ids) {
        ids.forEach(this::checkBackend);
        return removeByIds(ids);
    }

    @Override
    public Boolean delete(String id) {
        checkBackend(id);
        return removeById(id);
    }

    /**
     *  点击父级，
     *     启用，父子都改成启用的状态
     *     禁用，父子都改成禁用的状态
     *  点击子级
     *     启用，如果父级禁用，则需要开启
     *     禁用，只禁用子级
     *  实现方式：
     *  如果是禁用，则向下查看是否有子级，有子级，当前级和子级都禁用
     *  如果是启用，向下，有子级，当前级和子级都启用，向上，父级也都启用
     *
     * @param backend  后台类目Id
     * @param state 状态 属性状态，0为启用，1为禁用
     * @return
     */
    @Transactional
    @Override
    public Boolean saveState(ClassificationBackend backend, Integer state) {
        var id = backend.getId();
        // backend的层级或者是否存在子类
        if(state == BackendStatic.STATE_DISABLE ){
 //            只查找子级就可以了
            if(backend.getLayer() == MAX_LAYER){
                ClassificationBackend param = new ClassificationBackend();
                param.setId(id);
                param.setState(state);
                this.updateById(param);
                Stream.of(state).filter(s ->
                        s == BackendStatic.STATE_DISABLE).forEach(
                        s ->{
                            goodsService.undoBackend(id);
                        });
            }else{
                //  查询所有子级id  需要向下 需要判断是一级的话，向下查找，不是一级就向上向下查找
                this.updateStateUpAndDown(backend,state);
            }
        }else{
            // 启用，需要判断是一级的话，向下查找，不是一级就向上向下查找
            this.updateStateUpAndDown(backend,state);
        }
        return true;
    }

    /**
     * 向上或者向下修改
     *
     * @param backend
     * @param state
     */
    private void updateStateUpAndDown(ClassificationBackend backend, Integer state){
        var id = backend.getId();
        if(backend.getLayer() == MAX_LAYER){
            ClassificationBackend param = new ClassificationBackend();
            param.setId(id);
            param.setState(state);
            this.updateById(param);
        }else{
            // 向上，需将当前和所有的父级都启用，
            var pids = this.getFullPathIdList(id);
            ClassificationBackend param = new ClassificationBackend();
            param.setState(state);
            int count = this.getBaseMapper().update(param, Wrappers.<ClassificationBackend>lambdaQuery()
                    .in(ClassificationBackend::getId,pids ));
            if(count < 1){
                throw new BusinessException(ErrorMessageEnum.FAILURE);
            }
            // 向下 需将当前和所有的父级都启用，
            var cIds = this.getAllChilrenIds(id);
            if(CollectionUtils.isNotEmpty(cIds)){
                int countChilrend = this.getBaseMapper().update(param, Wrappers.<ClassificationBackend>lambdaQuery()
                        .in(ClassificationBackend::getId,cIds ));
                if(countChilrend < 1){
                    throw new BusinessException(ErrorMessageEnum.FAILURE);
                }
            }
        }

    }
    /**
     * 根据查询条件查询所有的后台类目信息
     *
     * @param queryVo
     * @return
     */
    @Override
    public List<ClassificationBackend> findAll(BackendQueryVo queryVo) {
        List<ClassificationBackend> result = new ArrayList<>();
        List<ClassificationBackend> backends = this.list(Wrappers.<ClassificationBackend>lambdaQuery()
                .eq(StrUtil.isNotEmpty(queryVo.getPid()), ClassificationBackend::getPid, queryVo.getPid())
                .in(CollUtil.isNotEmpty(queryVo.getIds()), ClassificationBackend::getId, queryVo.getIds())
                .eq(null != queryVo.getState(), ClassificationBackend::getState, queryVo.getState())
                .orderByDesc(ClassificationBackend::getUpdateTime));

        result = backends.stream().filter(backendEntity -> {
            var keep = new AtomicBoolean(false);
            Optional.ofNullable(queryVo.getNeedBrand())
                    .filter(Boolean.TRUE::equals).ifPresentOrElse(needBrand -> {
                        // 检查是否包含品牌
                        Stream.of(backendEntity.getLayer() == 1).forEach(layer -> {
                            // 第一层
                            AtomicInteger aiCount = new AtomicInteger(0);
                            // 检查自身
                            aiCount.set(
                                    backendBrandMapper.selectCount(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                                            .eq(ClassificationBackendBrand::getClassificationBackendId,
                                                    backendEntity.getId())));
                            // 检查子类
                            var subIds = this
                                    .list(Wrappers.<ClassificationBackend>lambdaQuery()
                                            .eq(ClassificationBackend::getPid, backendEntity.getId()))
                                    .stream()
                                    .map(ClassificationBackend::getId)
                                    .collect(Collectors.toList());
                            var queryIds = new ArrayList<>(subIds);
                            Optional.of(subIds).filter(CollUtil::isNotEmpty)
                                    .ifPresent(ids -> queryIds.addAll(
                                            list(Wrappers.<ClassificationBackend>lambdaQuery()
                                                    .in(ClassificationBackend::getPid, ids))
                                                    .stream()
                                                    .map(ClassificationBackend::getId)
                                                    .collect(Collectors.toList())));
                            Optional.of(queryIds).filter(CollUtil::isNotEmpty)
                                    .ifPresent(ids -> {
                                        var countSub = backendBrandMapper.selectCount(Wrappers
                                                .<ClassificationBackendBrand>lambdaQuery()
                                                .in(ClassificationBackendBrand::getClassificationBackendId, ids));
                                        aiCount.addAndGet(countSub);
                                    });
                            keep.set(aiCount.get() > 0);
                        });
                        Stream.of(backendEntity.getLayer() == 2).forEach(layer -> {
                            // 第二层
                            AtomicInteger aiCount = new AtomicInteger(0);
                            // 检查自身
                            aiCount.set(
                                    backendBrandMapper.selectCount(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                                            .eq(ClassificationBackendBrand::getClassificationBackendId,
                                                    backendEntity.getId())));
                            // 检查子类
                            var subIds = list(Wrappers.<ClassificationBackend>lambdaQuery()
                                    .eq(ClassificationBackend::getPid, backendEntity.getId())).stream()
                                    .map(ClassificationBackend::getId).collect(Collectors.toList());
                            Optional.of(subIds).filter(CollUtil::isNotEmpty).ifPresent(ids -> {
                                var countSub = backendBrandMapper
                                        .selectCount(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                                                .in(ClassificationBackendBrand::getClassificationBackendId, ids));
                                aiCount.addAndGet(countSub);
                            });
                            keep.set(aiCount.get() > 0);
                        });
                        Stream.of(backendEntity.getLayer() == 3).forEach(layer -> {
                            // 第三层
                            var count = backendBrandMapper.selectCount(Wrappers
                                    .<ClassificationBackendBrand>lambdaQuery()
                                    .eq(ClassificationBackendBrand::getClassificationBackendId, backendEntity.getId()));
                            keep.set(count > 0);
                        });
                    }, () -> keep.set(true));
            return keep.get();
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 根据pid分页查询类目信息
     * 
     * @param page     页码
     * @param pageSize 页尺寸
     * @param pid      父分类Id
     * @return
     */
    @Override
    public Page<ClassificationBackend> findByPage(Integer page, Integer pageSize, String pid) {
        var ipage = new Page<ClassificationBackend>(page, pageSize);
        return this.page(ipage, Wrappers.<ClassificationBackend>lambdaQuery().eq(StrUtil.isNotEmpty(pid),
                ClassificationBackend::getPid, pid));
    }

    public Page<ClassificationBackend> findByPage(Integer page, Integer pageSize, BackendQueryVo queryVo) {
        var ipage = new Page<ClassificationBackend>(page, pageSize);
        var lambdaQueryWrapper = Wrappers.<ClassificationBackend>lambdaQuery()
                .eq(StrUtil.isNotEmpty(queryVo.getPid()), ClassificationBackend::getPid, queryVo.getPid())
                .in(CollUtil.isNotEmpty(queryVo.getIds()), ClassificationBackend::getId, queryVo.getIds())
                .eq(null != queryVo.getState(), ClassificationBackend::getState, queryVo.getState());
        lambdaQueryWrapper.orderByDesc(ClassificationBackend::getCreateTime);
        return page(ipage, lambdaQueryWrapper);
    }

    @Override
    public Integer countBrands(String id) {
        return backendBrandMapper.selectCount(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                .eq(ClassificationBackendBrand::getClassificationBackendId, id));
    }

    @Override
    public BackendDetailVo findDetailById(String id) {
        var backend = getById(id);
        if (backend == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var resultVo = BeanUtil.toBean(backend, BackendDetailVo.class);
        // 处理父类目信息
        var parentProperties = new HashMap<Integer, List<PropertyGroupSimpleVo>>();
        var ppProperties = new HashMap<Integer, List<PropertyGroupSimpleVo>>();
        var parentBrands = new ArrayList<BrandSimpleVo>();
        var ppBrands = new ArrayList<BrandSimpleVo>();
        Optional.ofNullable(backend.getPid()).filter(pid -> StrUtil.isNotEmpty(pid) && !"0".equals(pid))
                .ifPresent(pid -> Optional.ofNullable(getById(pid)).ifPresent(parent -> {
                    parentProperties.putAll(parseProperties(parent.getId(), BackendStatic.RELATION_SOURCE_PARENT));
                    parentBrands.addAll(parseBrands(parent.getId(), BackendStatic.RELATION_SOURCE_PARENT));
                    // 获取父级信息
                    var parentVo = BeanUtil.toBean(parent, BackendSimpleVo.class);
                    Optional.ofNullable(parent.getPid()).filter(ppid -> StrUtil.isNotEmpty(ppid) && !"0".equals(ppid))
                            .ifPresent(ppid -> Optional.ofNullable(getById(ppid)).ifPresent(pparent -> {
                                ppProperties
                                        .putAll(parseProperties(pparent.getId(), BackendStatic.RELATION_SOURCE_PARENT));
                                ppBrands.addAll(parseBrands(pparent.getId(), BackendStatic.RELATION_SOURCE_PARENT));
                                parentVo.setParent(BeanUtil.toBean(pparent, BackendSimpleVo.class));
                            }));
                    resultVo.setParent(parentVo);
                }));
        // 处理关联信息
        var properties = parseProperties(id, BackendStatic.RELATION_SOURCE_SELF);
        // 合并关联信息
        var crucialProperties = new ArrayList<PropertyGroupSimpleVo>();
        var baseProperties = new ArrayList<PropertyGroupSimpleVo>();
        var extraProperties = new ArrayList<PropertyGroupSimpleVo>();
        var saleProperties = new ArrayList<PropertyGroupSimpleVo>();
        properties.forEach((key, value) -> {
            Stream.of(key).filter(PropertyStatic.TYPE_CRUCIAL::equals).forEach(k -> crucialProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_BASE::equals).forEach(k -> baseProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_EXTRA::equals).forEach(k -> extraProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_SALE::equals).forEach(k -> saleProperties.addAll(value));
        });
        parentProperties.forEach((key, value) -> {
            Stream.of(key).filter(PropertyStatic.TYPE_CRUCIAL::equals).forEach(k -> crucialProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_BASE::equals).forEach(k -> baseProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_EXTRA::equals).forEach(k -> extraProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_SALE::equals).forEach(k -> saleProperties.addAll(value));
        });
        ppProperties.forEach((key, value) -> {
            Stream.of(key).filter(PropertyStatic.TYPE_CRUCIAL::equals).forEach(k -> crucialProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_BASE::equals).forEach(k -> baseProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_EXTRA::equals).forEach(k -> extraProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_SALE::equals).forEach(k -> saleProperties.addAll(value));
        });
        resultVo.setCrucialProperties(crucialProperties);
        resultVo.setSaleProperties(saleProperties);
        resultVo.setExtraProperties(extraProperties);
        resultVo.setBaseProperties(baseProperties);
        // 处理关联品牌
        resultVo.setBrands(parseBrands(id, BackendStatic.RELATION_SOURCE_SELF));
        if (CollUtil.isNotEmpty(parentBrands)) {
            resultVo.getBrands().addAll(parentBrands);
        }
        if (CollUtil.isNotEmpty(ppBrands)) {
            resultVo.getBrands().addAll(ppBrands);
        }
        // 去重
        resultVo.setCrucialProperties(propertyDeduplication(resultVo.getCrucialProperties()));
        resultVo.setBaseProperties(propertyDeduplication(resultVo.getBaseProperties()));
        resultVo.setSaleProperties(propertyDeduplication(resultVo.getSaleProperties()));
        resultVo.setExtraProperties(propertyDeduplication(resultVo.getExtraProperties()));
        resultVo.setBrands(brandDeduplication(resultVo.getBrands()));
        return resultVo;
    }

    @Override
    public List<ClassificationBackendPropertyGroup> findAllBackendPropertyGroup(String backendId, Integer propertyType,
            String propertyGroupId) {
        boolean isNull = backendId == null && propertyType == null && propertyGroupId == null;
        var results = new ArrayList<ClassificationBackendPropertyGroup>();
        Stream.of(isNull).filter(Boolean.FALSE::equals)
                .forEach(in -> results.addAll(backendPropertyGroupMapper.selectList(Wrappers
                        .<ClassificationBackendPropertyGroup>lambdaQuery()
                        .eq(StrUtil.isNotEmpty(backendId),
                                ClassificationBackendPropertyGroup::getClassificationBackendId, backendId)
                        .eq(StrUtil.isNotEmpty(propertyGroupId), ClassificationBackendPropertyGroup::getPropertyGroupId,
                                propertyGroupId)
                        .eq(propertyType != null, ClassificationBackendPropertyGroup::getPropertyType, propertyType))));
        return results;
    }

    /**
     * 根据后台类目id获取其所有的父级类目信息
     * 获取后台分类链
     * 
     * @param backendId 后台分类id
     * @return
     */
    @Override
    public BackendSimpleVo findBackendLink(String backendId) {
        var arVo = new AtomicReference<BackendSimpleVo>();
        Optional.ofNullable(backendId).filter(StrUtil::isNotEmpty)
                .ifPresent(id -> Optional.ofNullable(getById(id))
                        .ifPresent(brand -> {
                            var vo = BeanUtil.toBean(brand, BackendSimpleVo.class);
                            Optional.ofNullable(brand.getPid()).filter(StrUtil::isNotEmpty)
                                    .ifPresent(pid -> Optional.ofNullable(getById(pid))
                                            .ifPresent(parent -> {
                                                var parentVo = BeanUtil.toBean(parent, BackendSimpleVo.class);
                                                Optional.ofNullable(parent.getPid()).filter(StrUtil::isNotEmpty)
                                                        .ifPresent(ppid -> Optional.ofNullable(getById(ppid))
                                                                .ifPresent(pparent ->
                                                                        parentVo.setParent(BeanUtil.toBean(pparent, BackendSimpleVo.class))
                                                                )
                                                        );
                                                vo.setParent(parentVo);
                                            }));
                            arVo.set(vo);
                        }));
        return arVo.get();
    }

    /**
     * 根据后台类目id获取对应的父级
     *
     * @param backendId
     * @return
     */
    @Override
    public String getBackendLinkId(String backendId) {
        BackendSimpleVo backend = this.findBackendLink(backendId);
        return getFullPathId(backend);
    }

    private List<String> getFullPathIdList(String backendId) {
        BackendSimpleVo backend = this.findBackendLink(backendId);
         return getFullPathIdList(backend);
    }
    @Override
    public List<BackendSimpleVo> getBackendLinkList(String backendId) {
        BackendSimpleVo backend = this.findBackendLink(backendId);
        List<BackendSimpleVo> backendSimpleVos = new ArrayList<>();
        getFullPathList(backend, backendSimpleVos);
        return backendSimpleVos;
    }

    /**
     * 根据三级后台类目查询list[一级类目，二级类目，三级类目]
     * @param threeLayerBackendId
     * @return
     */
    @Override
    public List<BackendByLayerListVo> getBackendListByLayer(String threeLayerBackendId) {
        List<BackendByLayerListVo> layerListVos = new ArrayList<>();
        Optional.ofNullable(threeLayerBackendId).filter(StrUtil::isNotEmpty)
                .ifPresent(id -> Optional.ofNullable(getById(id))
                        .ifPresent(backend -> {
                            // 三级的
                            var vo = BeanUtil.toBean(backend, BackendByLayerListVo.class);
                            Optional.ofNullable(backend.getPid()).filter(StrUtil::isNotEmpty)
                                    .ifPresent(pid -> Optional.ofNullable(getById(pid))
                                            .ifPresent(parent -> {
                                                // 二级的
                                                var parentVo = BeanUtil.toBean(parent, BackendByLayerListVo.class);
                                                Optional.ofNullable(parent.getPid()).filter(StrUtil::isNotEmpty)
                                                        .ifPresent(ppid ->
                                                                Optional.ofNullable(getById(ppid)).ifPresent(pparent ->
                                                                        // 一级的
                                                                        layerListVos.add(BeanUtil.toBean(pparent, BackendByLayerListVo.class))));
                                                layerListVos.add(parentVo);
                                            }));
                            layerListVos.add(vo);
                        }));

        return layerListVos;
    }

    /**
     * 查询后台类目id下的所有子类目id信息
     * 
     * @param backendId
     * @return
     */
    @Override
    public List<String> getChilrenIds(String backendId) {
        var subIds = new ArrayList<String>();
        Optional.ofNullable(
                list(Wrappers.<ClassificationBackend>lambdaQuery().eq(ClassificationBackend::getPid, backendId)))
                .ifPresent(backends -> {
                    subIds.addAll(backends.stream().map(ClassificationBackend::getId)
                            .collect(Collectors.toList()));
                });
        return subIds;
    }
    private List<String> getAllChilrenIds(String backendId) {
        var subIds = new ArrayList<String>();
        Optional.ofNullable(
                list(Wrappers.<ClassificationBackend>lambdaQuery().eq(ClassificationBackend::getPid, backendId)))
                .ifPresent(backends -> {
                    subIds.addAll(backends.stream().map(ClassificationBackend::getId)
                            .collect(Collectors.toList()));
                    backends.forEach(backend -> {
                        if(backend.getLayer() < MAX_LAYER){
                            subIds.addAll(getAllChilrenIds(backend.getId()));
                        }
                    });
                });
        return subIds;
    }
    /**
     * 递归获取全路径的后台类目信息
     *
     * @param backend
     * @param backendSimpleVos
     */
    private void getFullPathList(BackendSimpleVo backend, List<BackendSimpleVo> backendSimpleVos) {
        if (null == backend) {
            return;
        } else {
            backendSimpleVos.add(backend);
            if (null != backend.getParent()) {
                getFullPathList(backend.getParent(), backendSimpleVos);
            }
        }
    }

    private String getFullPathId(BackendSimpleVo backend) {
        String id = "";
        if (null == backend) {
            return id;
        }
        if (null == backend.getParent()) {
            id = backend.getId();
        } else {
            id = backend.getId() + "," + getFullPathId(backend.getParent());
        }
        return id;
    }
    private List<String> getFullPathIdList(BackendSimpleVo backend) {
        List<String> ids = new ArrayList<>();
        ids.add( backend.getId());
        if (null == backend) {
            return ids;
        }
        if (null == backend.getParent()) {
            ids.add( backend.getId());
        } else {
            ids.addAll(getFullPathIdList(backend.getParent()));
        }
        return ids;
    }
    /**
     * 去除重复的属性信息
     * 
     * @param propertyGroupSimpleVos
     * @return
     */
    private List<PropertyGroupSimpleVo> propertyDeduplication(List<PropertyGroupSimpleVo> propertyGroupSimpleVos) {
        var propertyMap = new HashMap<String, PropertyGroupSimpleVo>();
        propertyGroupSimpleVos.forEach(simpleVo -> Optional.ofNullable(propertyMap.get(simpleVo.getId()))
                .ifPresentOrElse(cache -> Stream.of(cache.getType())
                        .filter(type -> type.equals(BackendStatic.RELATION_SOURCE_SELF)).forEach(t -> {
                            cache.setType(simpleVo.getType());
                            propertyMap.put(cache.getId(), cache);
                        }), () -> propertyMap.put(simpleVo.getId(), simpleVo)));
        return new ArrayList<>(propertyMap.values());
    }

    private List<BrandSimpleVo> brandDeduplication(List<BrandSimpleVo> brandSimpleVos) {
        var propertyMap = new HashMap<String, BrandSimpleVo>();
        brandSimpleVos.forEach(
                simpleVo -> Optional.ofNullable(propertyMap.get(simpleVo.getId())).ifPresentOrElse(cache -> Stream
                        .of(cache.getType()).filter(t -> t.equals(BackendStatic.RELATION_SOURCE_SELF)).forEach(t -> {
                            cache.setType(simpleVo.getType());
                            propertyMap.put(cache.getId(), cache);
                        }), () -> propertyMap.put(simpleVo.getId(), simpleVo)));
        return new ArrayList<>(propertyMap.values());
    }

    /**
     * 转换品牌信息
     * @param id
     * @param relationSource
     * @return
     */
    private List<BrandSimpleVo> parseBrands(String id, Integer relationSource) {
        var brandList = backendBrandService.findAllBackendBrand(id, null);
        var results = new ArrayList<BrandSimpleVo>();
        if (CollUtil.isNotEmpty(brandList)) {
            var brands = goodsBrandMapper.selectBatchIds(brandList.stream()
                    .map(ClassificationBackendBrand::getBrandId)
                    .collect(Collectors.toList()));
            Optional.ofNullable(brands).ifPresent(bs -> results.addAll(bs.stream().map(brand -> {
                var vo = BeanUtil.toBean(brand, BrandSimpleVo.class);
                vo.setType(relationSource);
                return vo;
            }).collect(Collectors.toList())));
        }
        return results;
    }

    private Map<Integer, List<PropertyGroupSimpleVo>> parseProperties(String id, Integer relationSource) {
        var backendPropertyGroupList = findAllBackendPropertyGroup(id, null, null);
        var resultMap = new HashMap<Integer, List<PropertyGroupSimpleVo>>();
        if (CollUtil.isNotEmpty(backendPropertyGroupList)) {
            var propertyGroupMap = backendPropertyGroupList.stream()
                    .collect(Collectors.groupingBy(ClassificationBackendPropertyGroup::getPropertyType));
            Optional.ofNullable(propertyGroupMap.get(PropertyStatic.TYPE_CRUCIAL)).ifPresent(propertyGroups -> resultMap
                    .put(PropertyStatic.TYPE_CRUCIAL, getPropertyGroups(propertyGroups, relationSource)));
            Optional.ofNullable(propertyGroupMap.get(PropertyStatic.TYPE_BASE)).ifPresent(propertyGroups -> resultMap
                    .put(PropertyStatic.TYPE_BASE, getPropertyGroups(propertyGroups, relationSource)));
            Optional.ofNullable(propertyGroupMap.get(PropertyStatic.TYPE_SALE)).ifPresent(propertyGroups -> resultMap
                    .put(PropertyStatic.TYPE_SALE, getPropertyGroups(propertyGroups, relationSource)));
            Optional.ofNullable(propertyGroupMap.get(PropertyStatic.TYPE_EXTRA)).ifPresent(propertyGroups -> resultMap
                    .put(PropertyStatic.TYPE_EXTRA, getPropertyGroups(propertyGroups, relationSource)));
        }
        return resultMap;
    }

    private List<PropertyGroupSimpleVo> getPropertyGroups(List<ClassificationBackendPropertyGroup> cbpgs,
            Integer relationSource) {
        var groups = propertyGroupMapper.selectBatchIds(cbpgs.stream()
                .map(ClassificationBackendPropertyGroup::getPropertyGroupId).collect(Collectors.toList()));
        return groups.stream().map(group -> {
            var vo = BeanUtil.toBean(group, PropertyGroupSimpleVo.class);
            vo.setType(relationSource);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 检查后台分类名称是否重复
     *
     * @param backendVo 后台分类信息
     * @param curId     当前后台分类Id
     */
    private void backendNameDuplicate(BackendSaveVo backendVo, String curId) {
        var queryWrapper = Wrappers.<ClassificationBackend>lambdaQuery();
        queryWrapper.eq(ClassificationBackend::getName, backendVo.getName());
        Optional.ofNullable(backendVo.getParent()).map(BackendSimpleVo::getId)
                .ifPresent(pid -> queryWrapper.eq(ClassificationBackend::getPid, pid));
        Optional.ofNullable(curId).ifPresent(gid -> queryWrapper.ne(ClassificationBackend::getId, curId));
        Stream.of(count(queryWrapper)).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
        });
    }

    /**
     * 转换类目与属性组关联列表数据
     *
     * @param userId            当前操作用户id
     * @param backendId         后台类目id
     * @param propertyGroupList 属性组列表
     * @return 格式化数据
     */
    private List<ClassificationBackendPropertyGroup> parseBackendPropertyGroupList(String userId, String backendId,
            List<String> propertyGroupList, Integer propertyType) {
        return propertyGroupList.stream().map(groupId -> {
            var backendPropertyGroup = new ClassificationBackendPropertyGroup();
            backendPropertyGroup.setPropertyGroupId(groupId);
            backendPropertyGroup.setClassificationBackendId(backendId);
            backendPropertyGroup.setPropertyType(propertyType);
            backendPropertyGroup.setCreator(userId);
            return backendPropertyGroup;
        }).collect(Collectors.toList());
    }

    /**
     * 检查后台类目关联关系
     *
     * @param id 后台类目Id
     * @return 是否存在关联关系
     */
    private boolean checkBackend(String id) {
        // 检查是否有子分类
        boolean hasChildren = count(
                Wrappers.<ClassificationBackend>lambdaQuery().eq(ClassificationBackend::getPid, id)) > 0;
        // 检查是否有品牌关联
        boolean hasBrand = backendBrandMapper.selectCount(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                .eq(ClassificationBackendBrand::getClassificationBackendId, id)) > 0;
        // 检查是否有前台类目关联
        boolean hasFront = frontRelationMapper.selectCount(Wrappers
                .<ClassificationFrontRelation>lambdaQuery()
                .eq(ClassificationFrontRelation::getObjectType, id)
                .eq(ClassificationFrontRelation::getObjectType, FrontStatic.RELATION_TYPE_BACKEND)) > 0;
        boolean hasGoods = goodsSpuService.countByBackend(id) > 0;
        return hasChildren && hasBrand && hasFront && hasGoods;
    }

    /**
     * 根据前台类目id得到后台类目的id的set
     * 向下级查找
     * 
     * @param frontId
     * @return
     */
    @Override
    public HashSet<String> findBackendIdsByFrontIdDown(String frontId) {
        HashSet<String> frontBackEndIds = new HashSet<>();
        frontRelationService.findRelationByFrontId(frontId).forEach(frontRelation -> {
            // 前台类目关联的是后台类目 TODO 此处关联的后台类目及其子类目都得查询出来
            Stream.of(frontRelation).filter(fr -> fr.getObjectType() == FrontStatic.RELATION_TYPE_BACKEND)
                    .forEach(fr -> {
                        frontBackEndIds.add(fr.getObjectId());
                        // 并且需要查询出关联的后台类目id的子类目id
                        frontBackEndIds.addAll(this.getChilrenIds(fr.getObjectId()));
                    });
            // 前台类目关联的是销售属性
            Stream.of(frontRelation).filter(fr -> fr.getObjectType() == FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP)
                    .forEach(fr -> Optional.ofNullable(this.findAllBackendPropertyGroup(null, null, fr.getObjectId()))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(cbpgs -> cbpgs
                                    .forEach(cbpg -> frontBackEndIds.add(cbpg.getClassificationBackendId()))));
            // 关联的是品牌
            Stream.of(frontRelation).filter(fr -> fr.getObjectType() == FrontStatic.RELATION_TYPE_BRAND)
                    .forEach(fr -> Optional.ofNullable(backendBrandService.findAllBackendBrand(null, fr.getObjectId()))
                            .filter(CollUtil::isNotEmpty).ifPresent(cbbs -> cbbs
                                    .forEach(cbb -> frontBackEndIds.add(cbb.getClassificationBackendId()))));
        });
        return frontBackEndIds;
    }

    /**
     * 根据前台id找到关联的后台类目id及父级的类目id
     * 
     * @param frontId
     * @return
     */
    @Override
    public List<BackendSimpleVo> findBackendsByFrontIdUp(String frontId) {
        List<BackendSimpleVo> simpleVos = new ArrayList<>();
        // HashSet<String> frontBackEndIds = new HashSet<>();
        frontRelationService.findRelationByFrontId(frontId).forEach(frontRelation -> {
            // 前台类目关联的是后台类目 TODO 此处关联的后台类目及其子类目都得查询出来
            Stream.of(frontRelation).filter(fr -> fr.getObjectType() == FrontStatic.RELATION_TYPE_BACKEND)
                    .forEach(fr -> {
                        // frontBackEndIds.add(fr.getObjectId());
                        // 并且需要查询出关联的后台类目id的子类目id
                        simpleVos.addAll(this.getBackendLinkList(fr.getObjectId()));
                    });
        });
        return simpleVos;
    }

    /**
     * 根据一级类目的信息，获取一级下所有子级，并计算关联品牌、属性、spu的数量
     * @param records
     * @return
     */
    @Override
    public List<BackendPageListVo> getChildrenBackendByLayer1PageList(List<ClassificationBackend> records) {
        var datas = new ArrayList<BackendPageListVo>();

        records.forEach(backend -> {
            var backendVo = BeanUtil.toBean(backend, BackendPageListVo.class);

            // 处理父级信息
            Optional.ofNullable(backend.getPid()).filter(StrUtil::isNotEmpty)
                    .filter(parentId -> !"0".equals(parentId))
                    .ifPresent(parentId -> Optional.ofNullable(this.getById(parentId)).ifPresent(parent -> {
                        var parenVo = BeanUtil.toBean(parent, BackendSimpleVo.class);
                        Optional.ofNullable(parent.getPid()).filter(StrUtil::isNotEmpty)
                                .filter(ppid -> !"0".equals(ppid))
                                .ifPresent(ppId -> Optional.ofNullable(this.getById(ppId)).ifPresent(
                                        pp -> parenVo.setParent(BeanUtil.toBean(pp, BackendSimpleVo.class))));
                        backendVo.setParent(parenVo);
                    }));
            if (backend.getLayer() < MAX_LAYER) {
                var backends =  this.getChilrens(backend.getId());
                var children = this.getChildrenBackendByLayer1PageList(backends);
                backendVo.setChildren(children);
                // 统计品牌数量
                backendVo.setBrandCount(backendVo.getLayer() == MAX_LAYER ? this.countBrands(backend.getId())
                        : this.countBrandsByChildren(backendVo.getChildren()));
                // 统计关联属性组数量
                backendVo.setPropertyGroupCount(backendVo.getLayer() == MAX_LAYER
                        ? backendPropertyGroupService.countPropertyGroup(backend.getId())
                        : this.countPropertyGroupByChildren(backendVo.getChildren()));
                // 统计商品数量
                backendVo.setGoodsCount(
                        backendVo.getLayer() == MAX_LAYER ? goodsSpuService.countByBackend(backend.getId())
                                : this.countGoodsSpuByChildren(backendVo.getChildren()));
                // 还需要统计前台类目关联数据么？2022年3月16日15:55:52 TODO 待定
                // var count = backendVo.getBrandCount() + backendVo.getGoodsCount() +
                // backendVo.getPropertyGroupCount();
                // backendVo.setUseState(count > 0 ? true : false);
            }else if(backend.getLayer() ==  MAX_LAYER){
                // 统计品牌数量
                backendVo.setBrandCount(backendVo.getLayer() == MAX_LAYER ? this.countBrands(backend.getId())
                        : this.countBrandsByChildren(backendVo.getChildren()));
                // 统计关联属性组数量
                backendVo.setPropertyGroupCount(backendVo.getLayer() == MAX_LAYER
                        ? backendPropertyGroupService.countPropertyGroup(backend.getId())
                        : this.countPropertyGroupByChildren(backendVo.getChildren()));
                // 统计商品数量
                backendVo.setGoodsCount(
                        backendVo.getLayer() == MAX_LAYER ? goodsSpuService.countByBackend(backend.getId())
                                : this.countGoodsSpuByChildren(backendVo.getChildren()));
            }
            datas.add(backendVo);
        });
        return datas;
    }

    @Override
    public BackendDetailVo findDetailByIdNew(String id) {

        var backend = getById(id);
        if (backend == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var resultVo = BeanUtil.toBean(backend, BackendDetailVo.class);
        Optional.ofNullable(backend.getPid()).filter(pid -> StrUtil.isNotEmpty(pid) && !"0".equals(pid))
                .ifPresent(pid -> Optional.ofNullable(getById(pid)).ifPresent(parent -> {
                    var parentVo = BeanUtil.toBean(parent, BackendSimpleVo.class);
                    Optional.ofNullable(parent.getPid()).filter(ppid -> StrUtil.isNotEmpty(ppid) && !"0".equals(ppid))
                            .ifPresent(ppid -> Optional.ofNullable(getById(ppid)).ifPresent(pparent -> {
                                parentVo.setParent(BeanUtil.toBean(pparent, BackendSimpleVo.class));
                            }));
                    resultVo.setParent(parentVo);
                }));
        // 处理关联信息
        var properties = parseProperties(id, BackendStatic.RELATION_SOURCE_SELF);
        // 合并关联信息
        var crucialProperties = new ArrayList<PropertyGroupSimpleVo>();
        var baseProperties = new ArrayList<PropertyGroupSimpleVo>();
        var extraProperties = new ArrayList<PropertyGroupSimpleVo>();
        var saleProperties = new ArrayList<PropertyGroupSimpleVo>();
        properties.forEach((key, value) -> {
            Stream.of(key).filter(PropertyStatic.TYPE_CRUCIAL::equals).forEach(k -> crucialProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_BASE::equals).forEach(k -> baseProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_EXTRA::equals).forEach(k -> extraProperties.addAll(value));
            Stream.of(key).filter(PropertyStatic.TYPE_SALE::equals).forEach(k -> saleProperties.addAll(value));
        });
        resultVo.setCrucialProperties(crucialProperties);
        resultVo.setSaleProperties(saleProperties);
        resultVo.setExtraProperties(extraProperties);
        resultVo.setBaseProperties(baseProperties);
        // 处理关联品牌
        resultVo.setBrands(parseBrands(id, BackendStatic.RELATION_SOURCE_SELF));
        return resultVo;
    }
    /**
     *  计算子级中所有的是spu数量，适用新版本的统计数量
     * @param children
     * @return
     */
    private Integer countGoodsSpuByChildren(List<BackendPageListVo> children) {
        AtomicInteger count = new AtomicInteger(0);
        Optional.ofNullable(children)
                .ifPresent(list->{
                    count.set(list.stream()
                            .mapToInt(item -> item.getGoodsCount() == null ? 0 : item.getGoodsCount())
                            .sum());
                });
        return count.get();

    }
    /**
     *  计算子级中所有的属性数量，适用新版本的统计数量
     * @param children
     * @return
     */
    private Integer countPropertyGroupByChildren(List<BackendPageListVo> children) {
        AtomicInteger count = new AtomicInteger(0);

        Optional.ofNullable(children)
                .ifPresent(list->{
                    count.set(list.stream()
                            .mapToInt(item -> item.getPropertyGroupCount() == null ? 0 : item.getPropertyGroupCount())
                            .sum());
                });
        return count.get();
    }

    /**
     *  计算子级中所有的品牌数量，适用新版本的统计数量
     * @param children
     * @return
     */
    private Integer countBrandsByChildren(List<BackendPageListVo> children) {
        AtomicInteger count = new AtomicInteger(0);
        Optional.ofNullable(children)
                .ifPresent(list->{
                    count.set(list.stream()
                            .mapToInt(item -> item.getBrandCount() == null ? 0 : item.getBrandCount())
                            .sum());
                });
        return count.get();
    }

    /**
     * 递归获取子分类的信息
     * 用于获取子类信息
     * @param pid
     * @return
     */
    private List<ClassificationBackend> getChilrens(String pid) {
        List<ClassificationBackend> backends = this.list(Wrappers.<ClassificationBackend>lambdaQuery()
                .eq(StrUtil.isNotEmpty(
                        pid), ClassificationBackend::getPid, pid)
                .orderByAsc(ClassificationBackend::getId));
        return backends;
    }


    }
