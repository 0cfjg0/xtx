package com.itheima.xiaotuxian.service.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendBrand;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.entity.goods.GoodsBrand;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendBrandMapper;
import com.itheima.xiaotuxian.mapper.goods.GoodsBrandMapper;
import com.itheima.xiaotuxian.mapper.goods.GoodsSpuMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendBrandService;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.util.StringUtil;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.*;
import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:23 下午
 * @Description:
 */
@Service
public class GoodsBrandServiceImpl extends ServiceImpl<GoodsBrandMapper, GoodsBrand> implements GoodsBrandService {
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private ClassificationBackendBrandMapper backendBrandMapper;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private GoodsSpuMapper spuMapper;
    @Autowired
    private ClassificationBackendBrandService backendBrandService;

    @Transactional
    @Override
    public Boolean saveBrand(BrandSaveVo saveVo, String opUser) {
        var brand = BeanUtil.toBean(saveVo, GoodsBrand.class);
        Optional.ofNullable(brand.getId()).ifPresentOrElse(id ->
                        Optional.ofNullable(getById(id)).ifPresentOrElse(source ->
                                brandNameDuplicate(brand.getName(), id), () -> {
                            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                        })
                , () -> {
                    brandNameDuplicate(brand.getName(), null);
                    brand.setCreator(opUser);
                });
        // 处理logo信息
        Optional.ofNullable(saveVo.getLogo()).map(PictureSimpleVo::getId).ifPresent(id ->
                Optional.ofNullable(pictureService.findById(id)).ifPresent(picture ->
                        brand.setLogoId(picture.getId()))
        );
        // 处理品牌大图信息
        Optional.ofNullable(saveVo.getBrandImage()).map(PictureSimpleVo::getId).ifPresent(id ->
                Optional.ofNullable(pictureService.findById(id)).ifPresent(picture ->
                        brand.setBrandImageId(picture.getId()))
        );
        //处理产地信息
        Optional.ofNullable(saveVo.getProductionPlace()).filter(StrUtil::isNotEmpty).ifPresent(productionPlace -> {
            var places = productionPlace.split("/");
            Stream.of(places.length).filter(length -> length > 0).forEach(length -> brand.setProductionPlaceCountry(places[0]));
            Stream.of(places.length).filter(length -> length > 1).forEach(length -> brand.setProductionPlaceState(places[1]));
        });
        //处理首字母信息
        Optional.ofNullable(saveVo.getName())
                .filter(StrUtil::isNotEmpty)
                .ifPresent(name -> brand.setFirstWord(StringUtil.getFirstLetter(name)));
        //保存信息
        saveOrUpdate(brand);
        // 处理关联关系
        Optional.ofNullable(saveVo.getBackends()).filter(CollUtil::isNotEmpty).ifPresent(backends ->{
            //根据品牌id 删除关联关系  update by lvbencai 20230324
//            Map<String, Object> removeMap = new HashMap<>();
//            removeMap.put("brand_Id",saveVo.getId());
//            backendBrandService.removeByMap(removeMap);
            backendBrandService.remove(Wrappers.<ClassificationBackendBrand>lambdaQuery().eq(ClassificationBackendBrand::getBrandId,saveVo.getId()));
            backends.forEach(backend ->
                Optional.ofNullable(backend.getId()).filter(StrUtil::isNotEmpty).ifPresent(id ->
                    Optional.ofNullable(backendService.getById(id)).ifPresent(backendEntity -> {
                        var backendBrand = new ClassificationBackendBrand();
                        backendBrand.setBrandId(brand.getId());
                        backendBrand.setClassificationBackendId(backendEntity.getId());
                        backendBrandMapper.insert(backendBrand);
                    })
                )
            );
        });
        return true;
    }

    @Override
    public Boolean deleteBrand(String id) {
        // 检查是否被引用
        Stream.of(spuMapper.selectCount(Wrappers
                .<GoodsSpu>lambdaQuery()
                .eq(GoodsSpu::getBrandId, id))
        ).filter(count -> count > 0)
                .forEach(count -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_USED);
                });
        return removeById(id);
    }

    @Override
    public Boolean batchDeleteBrand(List<String> ids) {
        // 检查是否被引用
        Stream.of(spuMapper.selectCount(Wrappers
                .<GoodsSpu>lambdaQuery()
                .in(GoodsSpu::getBrandId, ids))
        ).filter(count -> count > 0)
                .forEach(count -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_USED);
                });
        return removeByIds(ids);
    }

    @Override
    public Page<BrandVo> findByPage(BrandQueryVo queryVo) {
        var page = new Page<GoodsBrand>(queryVo.getPage() == null || queryVo.getPage() == 0 ? 1 : queryVo.getPage(),
                queryVo.getPageSize() == null ||  queryVo.getPageSize() == 0 ? 10 : queryVo.getPageSize());
        var datPage = this.baseMapper.findByPage(page, queryVo);
        var datas = datPage.getRecords();
        var resultPage = new Page<BrandVo>(datPage.getCurrent(), datPage.getSize(), datPage.getTotal());
        var records = datas.stream().map(goodsBrand -> {
            var brandVo = BeanUtil.toBean(goodsBrand, BrandVo.class);
            //处理产地
            brandVo.setProductionPlace(this.getProductionPlaceByEntity(goodsBrand));
            //处理logo
            var logo = getPictureByEntity(goodsBrand.getLogoId());
            brandVo.setLogo(logo == null ? null : logo.getUrl());
            //处理后台类目关联
            brandVo.setBackends(this.getBackendsByEntity(goodsBrand.getId()));
            return brandVo;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public BrandDetailVo findDetailById(String id) {
        var brand = getById(id);
        if (brand == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var resultVo = BeanUtil.toBean(brand, BrandDetailVo.class);
        //处理产地
        resultVo.setProductionPlace(this.getProductionPlaceByEntity(brand));
        //处理logo
        resultVo.setLogo(getPictureByEntity(brand.getLogoId()));
        //处理品牌大图
        resultVo.setBrandImage(getPictureByEntity(brand.getBrandImageId()));
        //处理后台类目关联
        resultVo.setBackends(this.getBackendsByEntity(brand.getId()));
        return resultVo;
    }

    @Override
    public List<BrandSimpleVo> findAll(BrandQueryVo queryVo) {
        if(queryVo.getPage() == null || queryVo.getPage() == 0 ){
            queryVo.setPage(1);
        }
        if(queryVo.getPageSize() == null ||  queryVo.getPageSize() == 0 ){
            queryVo.setPageSize(10);
        }
        return this.baseMapper.findAll(queryVo).stream().map(brand -> {
            var vo = BeanUtil.toBean(brand, BrandSimpleVo.class);
            vo.setDesc(brand.getDescription());
            var place = new StringBuilder();
            if(StringUtils.isNoneBlank(brand.getProductionPlaceCountry())){
                place.append(brand.getProductionPlaceCountry());
            }
            if(StringUtils.isNoneBlank(brand.getProductionPlaceState())){
                place.append(brand.getProductionPlaceState());
            }
            vo.setPlace(place.toString());

            Optional.ofNullable(brand.getBrandImageId()).filter(StrUtil::isNotEmpty).ifPresent(id ->
                    Optional.ofNullable(pictureService.findById(id)).ifPresent(picture -> {
                        vo.setLogo(picture.getUrl());
                        vo.setPicture(picture.getUrl());
                    })
            );
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<GoodsBrand> findAllEntities(BrandQueryVo queryVo) {
        //根据前台类目查询品牌信息
        //原有的逻辑是前台关联品牌--这个地方改成前台关联后台类目，然后查找关联相关品牌 TODO ???
        return this.baseMapper.findAll(queryVo);
    }

    @Override
    public List<String> findAllProductionPlace() {
        return baseMapper.getAllProductionPlace();
    }

    @Override
    public List<String> findAllFirstWord() {
        return baseMapper.getAllFirstWord();
    }

    @Override
    public List<ClassificationFront> findAllFront() {
        return baseMapper.getAllFront();
    }

    @Override
    public List<BrandSimpleVo> findBrandsByBackendIds(HashSet<String> frontBackEndIds) {
        BrandQueryVo queryVo = new BrandQueryVo();
        List<BackendSimpleVo> backends = frontBackEndIds.stream().map(frontBackEndId ->{
            var vo = new BackendSimpleVo();
            vo.setId(frontBackEndId);
            return vo;
        }).collect(Collectors.toList());
        queryVo.setBackends(backends);
        return this.findAll(queryVo);
    }

    /**
     * 获取格式化产地
     *
     * @param goodsBrand 品牌信息
     * @return 格式化产地
     */
    private String getProductionPlaceByEntity(GoodsBrand goodsBrand) {
        StringBuilder placeBuilder = new StringBuilder();
        Optional.ofNullable(goodsBrand.getProductionPlaceCountry()).filter(StrUtil::isNotEmpty).ifPresent(placeBuilder::append);
        Optional.ofNullable(goodsBrand.getProductionPlaceState()).filter(StrUtil::isNotEmpty).ifPresent(place ->
                placeBuilder.append("/" + place));
        return placeBuilder.toString();
    }

    /**
     * 获取图片信息
     *
     * @param pictureId 图片id
     * @return 图片信息
     */
    private PictureSimpleVo getPictureByEntity(String pictureId) {
        AtomicReference<PictureSimpleVo> vo = new AtomicReference<>();
        Optional.ofNullable(pictureId).filter(StrUtil::isNotEmpty).ifPresent(id ->
                Optional.ofNullable(pictureService.findById(id)).ifPresent(picture ->
                        vo.set(BeanUtil.toBean(picture, PictureSimpleVo.class)))
        );
        return vo.get();
    }

    /**
     * 获取品牌的后台分类关联信息
     *
     * @param brandId 品牌id
     * @return 后台分类关联信息
     */
    private List<BackendSimpleVo> getBackendsByEntity(String brandId) {
        var results = new ArrayList<BackendSimpleVo>();
        Optional.ofNullable(backendBrandMapper.selectList(Wrappers
                .<ClassificationBackendBrand>lambdaQuery()
                .eq(ClassificationBackendBrand::getBrandId, brandId)))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(backendBrands -> {
                    var backends = new ArrayList<BackendSimpleVo>();
                    backendBrands.forEach(backendBrand ->
                            Optional.ofNullable(backendService.getById(backendBrand.getClassificationBackendId())).ifPresent(backend ->
                                    backends.add(BeanUtil.toBean(backend, BackendSimpleVo.class))));
                    results.addAll(backends);
                });
        return results;
    }

    /**
     * 检查品牌名称是否重复
     *
     * @param name  品牌名称
     * @param curId 当前品牌Id
     */
    private void brandNameDuplicate(String name, String curId) {
        Stream.of(
                count(Wrappers
                        .<GoodsBrand>lambdaQuery()
                        .ne(StrUtil.isNotEmpty(curId), GoodsBrand::getId, curId)
                        .eq(GoodsBrand::getName, name)
                )
        ).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
        });
    }
}
