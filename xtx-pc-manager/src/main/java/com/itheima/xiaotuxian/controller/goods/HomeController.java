package com.itheima.xiaotuxian.controller.goods;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.enums.HomeGoodsEnum;
import com.itheima.xiaotuxian.constant.statics.BusinessAdStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.business.BusinessAd;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.home.HomeHotRecommend;
import com.itheima.xiaotuxian.entity.marketing.MarketingTopic;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.exception.AuthException;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.business.BusinessAdService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.home.HomeHotRecommendService;
import com.itheima.xiaotuxian.service.marketing.MarketingOneStopService;
import com.itheima.xiaotuxian.service.marketing.MarketingTopicService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.record.RecordOrderSpuService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultMiniVo;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandQueryVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.home.CategoryGoodsVo;
import com.itheima.xiaotuxian.vo.home.InVogueVo;
import com.itheima.xiaotuxian.vo.home.IndexVo;
import com.itheima.xiaotuxian.vo.home.NewGoodsVo;
import com.itheima.xiaotuxian.vo.home.response.ActivitiesVo;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;
import com.itheima.xiaotuxian.vo.home.response.HotMutliVo;
import com.itheima.xiaotuxian.vo.home.response.HotRecommendVo;
import com.itheima.xiaotuxian.vo.marketing.OneStopVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Result;

/**
 * @author zsf
 * @date 2023年5月6日08:43:39
 */
@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController extends BaseController {
    @Autowired
    private BusinessAdService businessAdService;
    @Autowired
    private ClassificationFrontService frontService;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private MarketingOneStopService oneStopService;
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private MarketingTopicService topicService;
    @Autowired
    private HomeHotRecommendService hotRecommendService;
    @Autowired
    private RecordOrderSpuService recordOrderSpuService;
    @Autowired
    private GoodsSpuService goodsSpuService;


    @Value("${picture.head.resize:?quality=95&imageView}")
    private String headResize;

    /**
     * 首页-广告区域
     *
     * @return 广告信息
     */
    @GetMapping("/banner")
    public R Getbanner(@RequestParam Integer distributionSite) {
        List<BannerResultVo> getbanner = businessAdService.getbanner(distributionSite);
        return R.ok(getbanner);
    }

    /**
     * 首页-头部分类-PC
     *
     * @return 分类信息
     */
    @GetMapping("/category/head")
    public R getHead(){
        List<FrontResultVo> list = frontService.getHead();
        return R.ok(list);
    }


    /**
     * 首页-新鲜好物
     *
     * @return 新鲜好物
     */
    @GetMapping("/new")
//    public R getNewGoods() {
//        System.out.println("----------------------------------------------13331-------------");
//        List<GoodsItemResultVo> newGoods = goodsSpuService.getNewGoods();
//        System.out.println("----------------------------------------------13332-------------");
//        return R.ok(newGoods);
//    }
    public R<List<GoodsItemResultVo>> getGoodsItemResultVo(@RequestParam (name = "limit", defaultValue = "4") Integer limit){
        List<GoodsItemResultVo> goodsListByPublishTime = getGoodsListByPublishTime(limit,"desc",false);
        return R.ok(goodsListByPublishTime);
    }


    /**
     * 新鲜好物-PC --未使用
     *
     * @return 商品信息
     */


    /**
     * 新鲜好物-APP
     *
     * @return 商品信息
     */


    /**
     * 首页-人气推荐
     * PC端的调用信息
     *
     * @return 人气推荐信息
     */

    @GetMapping("/hot")
    public R getHot() {
        List<HotRecommendVo> list = hotRecommendService.getHot();
        return R.ok(list);
    }
    /**
     * 首页-人气推荐 小程序使用
     * 移动端的调用信息
     *
     * @return 人气推荐信息
     */


    /**
     * 一站买全
     *
     * @return 一站买全信息
     */
    @GetMapping("/oneStop")
    public R<List<OneStopVo>> getOneStop() {
        return R.ok(oneStopService.findAll(getClient()));
    }

    /**
     * 一站买全 -小程序
     * 和爆款推荐、特惠推荐做成相同的返回数据格式
     *
     * @return 一站买全信息
     */
    @GetMapping("/oneStop/mutli")
    public R<ActivitiesVo> getOneStopMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesVo activitiesVo = new ActivitiesVo();
        activitiesVo.setTitle("一站全买");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY,
                getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesVo>();
        ActivitiesVo subActivitiesVoOne = new ActivitiesVo();
        subActivitiesVoOne.setTitle("搞定熊孩子");
        var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
        if (CollUtil.isNotEmpty(subOnebanners)) {
            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
        }
        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setSummary(null);
        subTypes.add(subActivitiesVoOne);
        ActivitiesVo subActivitiesVoTwo = new ActivitiesVo();
        subActivitiesVoTwo.setTitle("家里不凌乱");
        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
        if (CollUtil.isNotEmpty(subTwobanners)) {
            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
        }
        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);
        ActivitiesVo subActivitiesVoThree = new ActivitiesVo();
        subActivitiesVoThree.setTitle("让音质更出众");
        var subThreebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoThree.getTitle()));
        if (CollUtil.isNotEmpty(subThreebanners)) {
            subActivitiesVoThree.setBannerPicture(subThreebanners.get(0).getImgUrl());
        }
        subActivitiesVoThree.setId(getMockId(subActivitiesVoThree.getTitle()));
        subActivitiesVoThree.setSummary(null);
        subTypes.add(subActivitiesVoThree);

        activitiesVo.setSubTypes(subTypes);
        if (StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(),
                subActivitiesVoTwo.getId(), subActivitiesVoThree.getId())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
        if (StringUtils.isNotBlank(subType)) {
            Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
            items.put(subType, pagerGoods);
        } else {
            subTypes.stream().forEach(subtypeobj -> {
                Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
                items.put(subtypeobj.getId(), pagerGoods);
            });
        }

        activitiesVo.setGoodsItems(items);
        return R.ok(activitiesVo);
    }

    /**
     * 首页-热门品牌
     *
     * @return 热门品牌
     */
@GetMapping("/brand")
public R getHotBrand(){
    List<BrandSimpleVo> list = brandService.getHotBrand();
    return R.ok(list);
}
    /**
     * 首页-产品区块
     *
     * @return 产品区块信息
     */
    @GetMapping("/goods")
    public R<List<CategoryGoodsVo>> getCategoryGoods() {
        List<CategoryGoodsVo> goodsVo = frontService.findAllValidFront("0").subList(0, 4).stream().map(front -> {
            var categoryGoods = BeanUtil.toBean(front, CategoryGoodsVo.class);
            var pick = RandomUtil.randomInt(HomeGoodsEnum.values().length);
            categoryGoods.setPicture(HomeGoodsEnum.values()[pick].getPicture());
            categoryGoods.setSaleInfo(HomeGoodsEnum.values()[pick].getSaleInfo());
            // 处理子类信息
            categoryGoods.setChildren(frontService.findAllValidFront(front.getId()).stream().map(subFront ->
                    BeanUtil.toBean(subFront, FrontSimpleVo.class)).collect(Collectors.toList()));
            // 处理商品信息
            var queryVo = new GoodsQueryPageVo();
            queryVo.setFrontId(front.getId());
            queryVo.setPage(1);
            //首页产品默认展示8个
            queryVo.setPageSize(8);
            categoryGoods.setGoods(searchGoodsService.findAllGoods(queryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
            return categoryGoods;
        }).collect(Collectors.toList());
        return R.ok(goodsVo);
    }

    /**
     * 首页-猜你喜欢（小程序调用）
     * 若用户有购买记录，则根据购买商品的前台分类id推荐
     *
     * @param page     页码
     * @param pageSize 页尺寸
     * @return 最新专题
     */
    @GetMapping("/goods/guessLike")
    public R<Pager<GoodsItemResultVo>> getGuessLike(@RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        SearchQueryVo queryVo = new SearchQueryVo();
        queryVo.setPage(page);
        queryVo.setPageSize(pageSize);
        try {
            //从token中获取memberId，根据用户获取其购买商品的前台类目id
            queryVo.setFrontIds(goodsSpuService.getFrontIdsByMemberId(getUserId()));
        } catch (AuthException e) {
            // 没有token的时候才查询用户id
        } catch (Exception e) {
            log.error("查询猜你喜欢报错", e);
        }
        var dataPage = searchGoodsService.searchByPage(queryVo);
        return R.ok(new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , dataPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())));
//        return R.ok(searchGoodsService.findAllGoods(queryVo).stream()
//                .map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient()))
//                .collect(Collectors.toList()));

    }

    /**
     * 首页-最新专题
     *
     * @param limit 数量限制
     * @return 最新专题
     */
    @GetMapping("/special")
    public R<List<MarketingTopic>> getNewTopics(@RequestParam(name = "limit", defaultValue = "3") Integer limit) {
        return R.ok(topicService.list(Wrappers.<MarketingTopic>lambdaQuery()
                .orderByDesc(MarketingTopic::getUpdateTime)
                .last("limit " + limit)));
    }

    /**
     * 爆款推荐
     *
     * @return 爆款推荐
     */
    @GetMapping("/inVogue")
    public R<InVogueVo> inVogue() {
        var goodsDayQueryVo = new HotGoodsQueryVo();
        goodsDayQueryVo.setStartTime(LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        goodsDayQueryVo.setLimit(10);
        var dayList = searchGoodsService.getHotGoods(goodsDayQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList());

        var goodsTotalQueryVo = new HotGoodsQueryVo();
        goodsTotalQueryVo.setLimit(10);
        var totalList = searchGoodsService.getHotGoods(goodsDayQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList());


        var goodsWeekQueryVo = new HotGoodsQueryVo();
        goodsWeekQueryVo.setStartTime(LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        goodsWeekQueryVo.setLimit(10);
        var weekList = searchGoodsService.getHotGoods(goodsWeekQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList());
        return R.ok(new InVogueVo(dayList, weekList, totalList));

        // return R.ok(new InVogueVo(
        //         searchGoodsService.getHotGoods(new HotGoodsQueryVo(LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, 10, null))
        //                 .
        //         searchGoodsService.getHotGoods(new HotGoodsQueryVo(LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, 10, null))
        //                 .stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()),
        //         searchGoodsService.getHotGoods(new HotGoodsQueryVo(null, null, 10, null))
        //                 .stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())
        // ));
    }

    /**
     * 爆款推荐
     *
     * @return 爆款推荐
     */
    @GetMapping("/inVogue/mutli")
    public R<ActivitiesVo> inVogueMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesVo activitiesVo = new ActivitiesVo();
        activitiesVo.setTitle("爆款推荐");
        activitiesVo.setId(String.valueOf(getMockId(activitiesVo.getTitle())));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesVo>();
        ActivitiesVo subActivitiesVoOne = new ActivitiesVo();
        subActivitiesVoOne.setTitle("24小时热榜");
        var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
        if (CollUtil.isNotEmpty(subOnebanners)) {
            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
        }
        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setSummary(null);
        subTypes.add(subActivitiesVoOne);
        ActivitiesVo subActivitiesVoTwo = new ActivitiesVo();
        subActivitiesVoTwo.setTitle("热销总榜");
        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
        if (CollUtil.isNotEmpty(subTwobanners)) {
            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
        }
        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);
        ActivitiesVo subActivitiesVoThree = new ActivitiesVo();
        subActivitiesVoThree.setTitle("人气周榜");
        var subThreebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoThree.getTitle()));
        if (CollUtil.isNotEmpty(subThreebanners)) {
            subActivitiesVoThree.setBannerPicture(subThreebanners.get(0).getImgUrl());
        }
        subActivitiesVoThree.setId(getMockId(subActivitiesVoThree.getTitle()));
        subActivitiesVoThree.setSummary(null);
        subTypes.add(subActivitiesVoThree);

        activitiesVo.setSubTypes(subTypes);

        if (StringUtils.isNotBlank(subType) &&
                !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(), subActivitiesVoTwo.getId(), subActivitiesVoThree.getId())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }

        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
        if (StringUtils.isNotBlank(subType)) {

            Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
            items.put(subType, pagerGoods);
        } else {
            subTypes.stream().forEach(subtypeobj -> {
                Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
                items.put(subtypeobj.getId(), pagerGoods);
            });
        }

        activitiesVo.setGoodsItems(items);
        return R.ok(activitiesVo);
    }

    /**
     * 首页-聚合-APP
     *
     * @return 聚合数据
     */
    @GetMapping("/index")
    public R<IndexVo> getIndex() {
        IndexVo vo = new IndexVo();
        // banner
        Integer channel = getShowClient();
        //   vo.setImageBanners(businessAdService.findBanner(channel, BusinessAdStatic.DISTRIBUTION_SITE_INDEX));
        // 商品分类
        List<FrontResultVo> frontResultVos = frontService.findCategory();
        //update by lbc 适应app显示两页的需求 显示14个，随机复制一部分，返回给前端
        // for(int i = frontResultVos.size(); i<14;i++){
        //     frontResultVos.add(frontResultVos.get(RandomUtil.randomInt(frontResultVos.size())));
        // }
        vo.setCategoryGrids(frontResultVos);
        // 商品分类
        // vo.setCategoryBanners(frontService.findCategoryAndGoods(8, getShowClient()));
        // 新鲜好物
        vo.setFreshGoods(getGoodsListByPublishTime(4, "desc", false));
        // 品牌
        var queryVo = new BrandQueryVo();
        queryVo.setLimit(4);
        vo.setHotBrands(brandService.findAll(queryVo));
        // 专题
        vo.setProjects(topicService.list(Wrappers.<MarketingTopic>lambdaQuery().orderByDesc(MarketingTopic::getUpdateTime).last("limit 2")));
        // 推荐列表
        vo.setHotRecommends(ConvertUtil.getRecommends());
        // 推荐商品
        if (CollUtil.isNotEmpty(vo.getCategoryBanners())) {
            vo.getCategoryBanners().forEach(category -> {
                var goodsQueryVo = new GoodsQueryPageVo();
                goodsQueryVo.setFrontId(category.getId());
                category.setGoods(searchGoodsService.findAllGoods(goodsQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
            });
        }
        return R.ok(vo);
    }

    /**
     * 首页-头部分类-小程序
     * pmd personal mobile device
     *
     * @return 分类信息
     */
    @GetMapping("/category/head/mutli")
    public R<List<FrontResultMiniVo>> getPmdCategoryHead() {
        List<FrontResultMiniVo> result = frontService.findAllValidFront("0")
                .stream().map(front -> {
                    var resultVo = BeanUtil.toBean(front, FrontResultMiniVo.class);
                    //处理图片
                    Optional.ofNullable(front.getPictureId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(pictureId -> resultVo.setIcon(pictureService.getPictureUrl(pictureId) + headResize));
                    return resultVo;
                }).collect(Collectors.toList());
        //模拟增加品牌的图标信息
        var brandHead = new FrontResultMiniVo();
        brandHead.setIcon(pictureService.getPictureUrl("1390110715762708482") + headResize);
        brandHead.setName("品牌");
        brandHead.setId("999999");
        result.add(brandHead);
        return R.ok(result);
    }


    /**
     * 首页-头部分类-app
     * pmd personal mobile device
     * 单独查询一级分类，app端为了能够实现居中效果，后台复制数据返回前段
     *
     * @return 分类信息
     */
    @GetMapping("/category/head/app")
    public R<List<FrontResultMiniVo>> getAppPmdCategoryHead() {
        List<FrontResultMiniVo> result = frontService.findAllValidFront("0")
                .stream().map(front -> {
                    var resultVo = BeanUtil.toBean(front, FrontResultMiniVo.class);
                    //处理图片
                    Optional.ofNullable(front.getPictureId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(pictureId -> resultVo.setIcon(pictureService.getPictureUrl(pictureId) + headResize));
                    return resultVo;
                }).collect(Collectors.toList());
        result.addAll(result.stream().map(e -> {
                    FrontResultMiniVo d = new FrontResultMiniVo();
                    BeanUtil.copyProperties(e, d);
                    return d;
                }).collect(Collectors.toList())
        );
        return R.ok(result);
    }

    /**
     * 首页-分类-APP
     * 未使用 2023年5月12日11:08:00
     *
     * @return 分类信息
     */
    @GetMapping("/category")
    public R<List<FrontResultVo>> getCategory() {
        return R.ok(frontService.findCategory());
    }

    /**
     * 首页-通过分类找商品-APP
     *
     * @param page     页码
     * @param pageSize 页尺寸
     * @return 商品列表
     */
    @GetMapping("/category/{id}/goods")
    public R<List<GoodsItemResultVo>> getGoodsByCategory(@PathVariable(name = "id") String id, @RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        var queryVo = new GoodsQueryPageVo();
        queryVo.setFrontId(id);
        queryVo.setPage(page);
        queryVo.setPageSize(pageSize);
        return R.ok(searchGoodsService.findAllGoods(queryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
    }


    /**
     * 首页-获取推荐商品-APP
     *
     * @return 推荐商品
     */
    @GetMapping("/index/userLike")
    public R<List<GoodsItemResultVo>> getUserLike(@RequestParam(name = "categoryId", required = false) String categoryId
            , @RequestParam(name = "spuId", required = false) String spuId
            , @RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (StrUtil.isEmpty(categoryId) && StrUtil.isEmpty(spuId)) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        var goodsQueryVo = new GoodsQueryPageVo();
        goodsQueryVo.setFrontId(categoryId);
        goodsQueryVo.setId(spuId);
        goodsQueryVo.setPage(page);
        goodsQueryVo.setPageSize(pageSize);
        return R.ok(searchGoodsService.findAllGoods(goodsQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
    }

    /**
     * 以发布时间为基准，获取商品列表
     *
     * @param limit      数量限制
     * @param sortMethod 排序方法
     * @param isPre      是否为预售
     * @return 商品列表
     */
    private List<GoodsItemResultVo> getGoodsListByPublishTime(Integer limit, String sortMethod, Boolean isPre) {
        var page = 1;
        var pageSize = limit;
        return searchGoodsService.findAllGoodsWithPublishTime(page, pageSize, sortMethod, isPre)
                .stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList());
    }

    /**
     * 特惠推荐-商品列表-小程序使用
     *
     * @return 特惠推荐信息
     */
    @GetMapping("/preference/mutli")
    public R<ActivitiesVo> getPreferenceMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesVo activitiesVo = new ActivitiesVo();
        activitiesVo.setTitle("特惠推荐");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesVo>();
        ActivitiesVo subActivitiesVoOne = new ActivitiesVo();
        subActivitiesVoOne.setTitle("抢先尝鲜");
        var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
        if (CollUtil.isNotEmpty(subOnebanners)) {
            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
        }
        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setSummary(null);
        subTypes.add(subActivitiesVoOne);
        ActivitiesVo subActivitiesVoTwo = new ActivitiesVo();
        subActivitiesVoTwo.setTitle("新品预告");
        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
        if (CollUtil.isNotEmpty(subTwobanners)) {
            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
        }
        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);

        activitiesVo.setSubTypes(subTypes);

        if (StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(), subActivitiesVoTwo.getId())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }

        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
        if (StringUtils.isNotBlank(subType)) {
            Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
            items.put(subType, pagerGoods);
        } else {
            subTypes.stream().forEach(subtypeobj -> {
                Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", null);
                items.put(subtypeobj.getId(), pagerGoods);
            });
        }

        activitiesVo.setGoodsItems(items);
        return R.ok(activitiesVo);
    }

    /**
     * 新鲜好物-商品列表-小程序使用
     *
     * @return 新鲜好物信息
     */
    @GetMapping("/new/mutli")
    public R<List<HotMutliVo>> getNewMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesVo activitiesVo = new ActivitiesVo();
        activitiesVo.setTitle("新鲜好物");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesVo>();
        ActivitiesVo subActivitiesVoOne = new ActivitiesVo();
        subActivitiesVoOne.setTitle("抢先尝鲜");
        var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
        if (CollUtil.isNotEmpty(subOnebanners)) {
            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
        }
        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setSummary(null);
        subTypes.add(subActivitiesVoOne);
        ActivitiesVo subActivitiesVoTwo = new ActivitiesVo();
        subActivitiesVoTwo.setTitle("新品预告");
        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
        if (CollUtil.isNotEmpty(subTwobanners)) {
            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
        }
        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);

        activitiesVo.setSubTypes(subTypes);

        if (StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(), subActivitiesVoTwo.getId())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }

        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
        if (StringUtils.isNotBlank(subType)) {
            Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", "publishTime");
            items.put(subType, pagerGoods);

        } else {
            subTypes.stream().forEach(subtypeobj -> {
                Pager<GoodsItemResultVo> pagerGoods = getPageGoodsByEsGoods(page, pageSize, "自有", "publishTime");
                items.put(subtypeobj.getId(), pagerGoods);
            });
        }

        activitiesVo.setGoodsItems(items);
        return R.ok(activitiesVo);
    }

    private String getMockId(String name) {
        int hashCode = name.hashCode();
        String id = String.valueOf(Math.abs(hashCode));
        return id;
    }

    /**
     * @param {Integer} page
     * @param {Integer} pageSize
     * @param {String}  keyword
     * @param {String}  sortField
     * @return {*}
     * @description:
     * @author: lbc
     */
    private Pager<GoodsItemResultVo> getPageGoodsByEsGoods(Integer page, Integer pageSize, String keyword, String sortField) {
        SearchQueryVo queryVo = new SearchQueryVo();
        queryVo.setPage(page);
        queryVo.setPageSize(pageSize);
        if (StringUtils.isNotBlank(keyword)) {
            queryVo.setKeyword(keyword);
        }
        if (StringUtils.isNotBlank(sortField)) {
            queryVo.setSortField(sortField);
        }
        var dataPage = searchGoodsService.searchByPage(queryVo);
        List<EsGoods> pageEsGoods = dataPage.getRecords();
        // 改变集合的顺序TODO
        Collections.shuffle(pageEsGoods);
        return new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , pageEsGoods.stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient()))
                .collect(Collectors.toList()));

    }


}
