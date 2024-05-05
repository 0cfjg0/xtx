package com.itheima.xiaotuxian.controller.goods;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.BusinessAdStatic;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.PropertyStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.property.PropertyGroup;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.business.BusinessAdService;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontRelationService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.service.property.PropertyMainService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.classification.*;
import com.itheima.xiaotuxian.vo.goods.brand.BrandQueryVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;
import com.itheima.xiaotuxian.vo.property.*;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * category控制层
 * @author 曾少峰
 * add 注释 by bencai.lv
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Autowired
    private ClassificationFrontService frontService;
    @Autowired
    private ClassificationFrontRelationService frontRelationService;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private PropertyMainService propertyMainService;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private BusinessAdService businessAdService;

    /**
     *  二级类目-PC和小程序、APP
     * 根据一级类目查询二级类目信息
     * @param id 类目Id
     * @return 一级分类信息
     */
    @GetMapping
    public R<CategoryVo> findTop(@RequestParam(name = "id") String id) {
        var simpleFront = frontService.findSimpleById(id);
        //不能传
        if(simpleFront.getLayer() != 1){
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        var result = BeanUtil.toBean(simpleFront, CategoryVo.class);
        result.setChildren(frontService.findAllValidFront(result.getId()).stream().map(subFront -> {
            var sub = BeanUtil.toBean(subFront, SubCategoryVo.class);
            //处理图片信息
            Optional.ofNullable(subFront.getPictureId()).filter(StrUtil::isNotEmpty).ifPresent(pictureId ->
                    Optional.ofNullable(pictureService.findById(pictureId)).ifPresent(picture -> sub.setPicture(picture.getUrl())));
            //处理商品信息
            var queryVo = new GoodsQueryPageVo();
            queryVo.setPageSize(5);
            queryVo.setFrontId(subFront.getId());
            sub.setGoods(searchGoodsService.findAllGoods(queryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
            return sub;
        }).collect(Collectors.toList()));
        // 2023年9月23日14:57:06 修改，增加轮播图，手机app端，分类页面显示轮播图
        if (CommonStatic.REQUEST_CLIENT_APP.equals(getClient())) {
          //  List<BannerResultVo> bannerResultVos =  businessAdService.findBanner(getShowClient(),BusinessAdStatic.DISTRIBUTION_SITE_CLASSIFICATION);
         //   result.setBanners(bannerResultVos);
        }
        return R.ok(result);
    }

    /**
     * 二级类目-筛选条件-PC
     * 此处根据前台类目子类目id查询出当前子类目的商品
     * 子类目和父类目的相关类目信息，品牌信息，属性信息
     * @param id 类目Id
     * @return 筛选条件
     */
    @GetMapping("/sub/filter")
    public R<SubCategoryVo> subFilter(@RequestParam(name = "id") String id) {
        var simpleFront = frontService.findSimpleById(id);
        if (simpleFront == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (simpleFront.getLayer() != 2) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        var result = BeanUtil.toBean(simpleFront, SubCategoryVo.class);
        var frontIds = new ArrayList<String>();
        //处理父类信息
        Optional.ofNullable(simpleFront.getParent()).ifPresent(parent -> {
            result.setParentId(parent.getId());
            result.setParentName(parent.getName());
            frontIds.add(parent.getId());
        });
        //处理子类信息
        Optional.ofNullable(frontService.findAllValidFront(result.getParentId()))
                .filter(CollUtil::isNotEmpty).ifPresent(subFronts ->
                    result.setCategories(subFronts.stream().map(
                        front -> {
                            var vo = BeanUtil.toBean(front, FrontSimpleVo.class);
                            frontIds.add(vo.getId());
                            return vo;
                        })
                        .collect(Collectors.toList())));
        //处理关联信息
        var brands = new ArrayList<BrandSimpleVo>();
        var properties = new ArrayList<PropertyGroupVo>();

        //根据前台id查询当前类目下所有的goods
        GoodsQueryPageVo goodsQueryVo = new GoodsQueryPageVo();
        //查询不重复的后台类目id（根据前台类目id查找到关联的后台类目id及父级后台类目id）
//        HashSet<String> frontBackendIds = new HashSet<String>();
//        Optional.ofNullable(id).filter(StrUtil::isNotEmpty).ifPresent(fId -> {
//            frontBackendIds.addAll(backendService.findBackendIdsByFrontIdUp(id));
//            Stream.of(frontBackendIds).filter(CollUtil::isEmpty).forEach(ids -> goodsQueryVo.setBan("-1000"));
//        });
//        goodsQueryVo.setPageSize(20);
//        goodsQueryVo.setPage(1);
//        var pageResult = new Page<GoodsSpu>(goodsQueryVo.getPage() == null ? 1 : goodsQueryVo.getPage(), goodsQueryVo.getPageSize() == null ? 10 : goodsQueryVo.getPageSize());
//        Page<GoodsSpu> goodsPage = goodsSpuService.getPageData(goodsQueryVo,pageResult,frontBackEndIds);
//        result.setGoods(
//                goodsPage.getRecords().stream()
//                        .map(goodSpu -> ConvertUtil.convertGoodsItem(goodSpu, goodsService.getSpuPicture(goodSpu.getId(), getShowClient()), null, goodsService.getSpuPrice(goodSpu.getId())) )
//                        .collect(Collectors.toList())
//        );
        //处理商品信息
        var esqueryVo = new GoodsQueryPageVo();
        esqueryVo.setPageSize(20);
        esqueryVo.setFrontId(id);
        result.setGoods(searchGoodsService.findAllGoods(esqueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));

        Optional.ofNullable(frontRelationService.findRelationByFrontId(result.getId()))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(relations ->
                relations.forEach(relation -> {
                    // 处理属性组信息
                    Stream.of(relation).filter(r -> FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP == r.getObjectType()).forEach(r ->
                            Optional.ofNullable(propertyGroupService.getById(r.getObjectId())).ifPresent(propertyGroup -> {
                                var simpleVo = BeanUtil.toBean(propertyGroup, PropertyGroupVo.class);
                                //处理属性信息
                                var queryVo = new PropertyQueryVo();
                                queryVo.setGroupId(propertyGroup.getId());
                                Optional.ofNullable(
                                        propertyMainService.findAll(queryVo)).filter(CollUtil::isNotEmpty)
                                                    .ifPresent(propertyMains ->
                                                             simpleVo.setProperties(
                                                                     propertyMains.stream()
                                                                             .map(propertyMain -> BeanUtil.toBean(propertyMain, PropertySimpleVo.class))
                                                                             .collect(Collectors.toList())));
                                properties.add(simpleVo);
                            }));
                    // 处理品牌信息 TODO 后续，前台类目不关联品牌信息
                    Stream.of(relation)
                            .filter(r -> FrontStatic.RELATION_TYPE_BRAND == r.getObjectType())
                            .forEach(r ->
                                Optional.ofNullable(brandService.getById(r.getObjectId())).ifPresent(brand -> {
                                    var simpleVo = BeanUtil.toBean(brand, BrandSimpleVo.class);
                                    Optional.ofNullable(brand.getLogoId())
                                            .filter(StrUtil::isNotEmpty)
                                            .ifPresent(logoId ->
                                                Optional.ofNullable(pictureService.findById(logoId))
                                                        .ifPresent(picture -> {
                                                            simpleVo.setLogo(picture.getUrl());
                                                            simpleVo.setPicture(picture.getUrl());
                                            }));
                                    brands.add(simpleVo);
                            }));
                }));
        //2023-04-26增加 根据前台类目查找的后台类目，继续关联的品牌信息
        //查询不重复的后台类目id（根据前台类目id查找到关联的后台类目id及父级后台类目id）
        var backends = backendService.findBackendsByFrontIdUp(id);

        BrandQueryVo queryVo = new BrandQueryVo();
        queryVo.setBackends(backends);
        brands.addAll(brandService.findAll(queryVo));
        result.setBrands(brands);
        //根据后台类目及父级类目查询关联的属性信息
        PropertyGroupQueryVo propertyGroupQueryVo = new PropertyGroupQueryVo();
        propertyGroupQueryVo.setBackends(backends);
        propertyGroupQueryVo.setState(PropertyStatic.STATE_NORMAL);
        propertyGroupQueryVo.setPropertyType(PropertyStatic.TYPE_SALE);
        List<PropertyGroup>  vo = propertyGroupService.findAll(propertyGroupQueryVo);
        List<PropertyMainVo> propertyVos = new ArrayList<>();
        vo.stream().forEach(group->{
            propertyVos.addAll(propertyMainService.findMainAndValueByGroupId(group.getId(),PropertyStatic.STATE_NORMAL));
        });
        result.setSaleProperties(propertyVos);

        return R.ok(result);
    }

    /**
     * 一级分类列表-APP
     *
     * @return 分类列表
     */
    @GetMapping("/top")
    public R<List<CategoryMultiVo>> findTop() {
        List<CategoryMultiVo> categoryMultiVos = frontService.findAllValidFront("0").stream().map(front -> {
            var vo = BeanUtil.toBean(front, CategoryMultiVo.class);
            //List<BannerResultVo> bannerResultVos =  businessAdService.findBanner(getShowClient(),BusinessAdStatic.DISTRIBUTION_SITE_CLASSIFICATION);
            List<BannerResultVo> bannerResultVos = null;
            List<ImageBannerVo> imageBannerVos = bannerResultVos.stream().map(bannerResultVo->{
                ImageBannerVo imageBannerVo = BeanUtil.toBean(bannerResultVo, ImageBannerVo.class);
                imageBannerVo.setTitle("分类轮播图");
                return imageBannerVo;
            }).collect(Collectors.toList());
            vo.setImageBanners(imageBannerVos);
            // 处理图片信息
            Optional.ofNullable(front.getPictureId()).filter(StrUtil::isNotEmpty).ifPresent(pictureId ->
                    Optional.ofNullable(pictureService.findById(pictureId)).ifPresent(picture -> vo.setPicture(picture.getUrl())));
            // 处理子集
            vo.setChildren(frontService.findAllValidFront(front.getId()).stream().map(subFront -> {
                var sub = BeanUtil.toBean(subFront, SubCategoryVo.class);
                // 处理图片信息
                Optional.ofNullable(subFront.getPictureId()).filter(StrUtil::isNotEmpty).ifPresent(pictureId ->
                        Optional.ofNullable(pictureService.findById(pictureId)).ifPresent(picture -> sub.setPicture(picture.getUrl())));
                //处理商品信息
                var queryVo = new GoodsQueryPageVo();
                queryVo.setFrontId(subFront.getId());
                sub.setGoods(searchGoodsService.findAllGoods(queryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
                return sub;
            }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
        return R.ok(categoryMultiVos);
    }

    /**
     * 商品列表
     *
     * @param queryVo 查询条件
     * @return 商品列表
     */
    @PostMapping("/goods")
    public R<Pager<GoodsItemResultVo>> findGoods(@RequestBody SearchQueryVo queryVo) {
        var dataPage = searchGoodsService.searchByPage(queryVo);
        return R.ok(new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , dataPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())));
    }

    /**
     * 商品列表--临时接口，返回所有的商品，支持分页
     *
     * @param queryVo 查询条件
     * @return 商品列表
     */
    @PostMapping("/goods/temporary")
    public R<Pager<GoodsItemResultVo>> findTemporaryGoods(@RequestBody SearchQueryVo queryVo) {
        queryVo.setCategoryId(null);
        queryVo.setBrandId(null);
        queryVo.setAttrs(null);
        queryVo.setSortField(null);
        queryVo.setSortMethod("desc");
        var dataPage = searchGoodsService.searchByPage(queryVo);
        return R.ok(new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , dataPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())));
    }
    /**
     * 商品列表-APP
     *
     * @param queryVo 查询条件
     * @return 商品列表
     */
    @PostMapping("/goods/mutli")
    public R<GoodsItemMultiVo> findGoodsMulti(@RequestBody SearchQueryVo queryVo) {
        var result = new GoodsItemMultiVo();
        // 处理商品分页数据
        var dataPage = searchGoodsService.searchByPage(queryVo);
        result.setPageData(new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , dataPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())));
        // 处理品牌数据
        // var brandQueryVo = new BrandQueryVo();
        // Optional.ofNullable(queryVo.getCategoryId()).filter(StrUtil::isNotEmpty).ifPresent(brandQueryVo::setFrontId);
        //查询不重复的后台类目id（根据前台类目id查找到关联的后台类目id及父级后台类目id）
        var backends = backendService.findBackendsByFrontIdUp(queryVo.getCategoryId());
        BrandQueryVo brandQueryVo = new BrandQueryVo();
        brandQueryVo.setBackends(backends);
        result.setBrands(brandService.findAll(brandQueryVo));
        return R.ok(result);
    }
}
