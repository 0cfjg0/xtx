package com.itheima.xiaotuxian.controller.v8;

import cn.hutool.core.collection.CollUtil;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.BusinessAdStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.business.BusinessAdService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.home.response.ActivitiesV8Vo;
import com.itheima.xiaotuxian.vo.home.response.ActivitiesV8Vo;
import com.itheima.xiaotuxian.vo.home.response.HotMutliVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/")
public class HotV8Controller extends BaseController {
    @Autowired
    private BusinessAdService businessAdService;
    @Autowired
    private SearchGoodsService searchGoodsService;

    /**
     * 爆款推荐
     *
     * @return 爆款推荐
     */
    @GetMapping("/hot/inVogue")
    public R<ActivitiesV8Vo> inVogueMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesV8Vo activitiesVo = new ActivitiesV8Vo();
        activitiesVo.setTitle("爆款推荐");
        activitiesVo.setId(String.valueOf(getMockId(activitiesVo.getTitle())));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);

        var subTypes = new ArrayList<ActivitiesV8Vo>();

        ActivitiesV8Vo subActivitiesVoOne = new ActivitiesV8Vo();
        subActivitiesVoOne.setTitle("24小时热榜");
//        var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
//        if (CollUtil.isNotEmpty(subOnebanners)) {
//            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
//        }
//        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setId("772990341");
        subActivitiesVoOne.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("772990341")) {
            subActivitiesVoOne.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }

        subTypes.add(subActivitiesVoOne);

        ActivitiesV8Vo subActivitiesVoTwo = new ActivitiesV8Vo();
        subActivitiesVoTwo.setTitle("热销总榜");
//        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
//        if (CollUtil.isNotEmpty(subTwobanners)) {
//            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
//        }
        if (StringUtils.isBlank(subType) || subType.equals("772990342")) {

            subActivitiesVoTwo.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }

//        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setId("772990342");
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);
        ActivitiesV8Vo subActivitiesVoThree = new ActivitiesV8Vo();
        subActivitiesVoThree.setTitle("人气周榜");
//        var subThreebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoThree.getTitle()));
//        if (CollUtil.isNotEmpty(subThreebanners)) {
//            subActivitiesVoThree.setBannerPicture(subThreebanners.get(0).getImgUrl());
//        }
//        subActivitiesVoThree.setId(getMockId(subActivitiesVoThree.getTitle()));
        subActivitiesVoThree.setId("772990343");
        subActivitiesVoThree.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("772990343")) {
            subActivitiesVoThree.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoThree);
        activitiesVo.setSubTypes(subTypes);
        return R.ok(activitiesVo);
    }


    /**
     * 一站买全 -小程序
     * 和爆款推荐、特惠推荐做成相同的返回数据格式
     *
     * @return 一站买全信息
     */
    @GetMapping("/hot/oneStop")
    public R<ActivitiesV8Vo> getOneStopMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesV8Vo activitiesVo = new ActivitiesV8Vo();
        activitiesVo.setTitle("一站全买");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY,
                getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesV8Vo>();
        ActivitiesV8Vo subActivitiesVoOne = new ActivitiesV8Vo();
        subActivitiesVoOne.setTitle("搞定熊孩子");
//        var subOnebanners = businessAdService.findActivityBanner(getShowClient(),BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
//        if (CollUtil.isNotEmpty(subOnebanners)) {
//            subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
//        }
//        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setId("872990341");
        subActivitiesVoOne.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("872990341")) {
            subActivitiesVoOne.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoOne);

        ActivitiesV8Vo subActivitiesVoTwo = new ActivitiesV8Vo();
        subActivitiesVoTwo.setTitle("家里不凌乱");
//        var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
//        if (CollUtil.isNotEmpty(subTwobanners)) {
//            subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
//        }
//        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setId("872990342");
        if (StringUtils.isBlank(subType) || subType.equals("872990342")) {
            subActivitiesVoTwo.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subActivitiesVoTwo.setSummary(null);
        subTypes.add(subActivitiesVoTwo);


        ActivitiesV8Vo subActivitiesVoThree = new ActivitiesV8Vo();
        subActivitiesVoThree.setTitle("让音质更出众");
//            var subThreebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoThree.getTitle()));
//            if (CollUtil.isNotEmpty(subThreebanners)) {
//                subActivitiesVoThree.setBannerPicture(subThreebanners.get(0).getImgUrl());
//            }
//        subActivitiesVoThree.setId(getMockId(subActivitiesVoThree.getTitle()));
        subActivitiesVoThree.setId("872990343");
        subActivitiesVoThree.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("872990343")) {
            subActivitiesVoThree.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoThree);

        activitiesVo.setSubTypes(subTypes);
//        if (StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(),
//                subActivitiesVoTwo.getId(), subActivitiesVoThree.getId())) {
//            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
//        }
//        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
//        if (StringUtils.isNotBlank(subType)) {
//            Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有",null);
//            items.put(subType, pagerGoods);
//        } else {
//            subTypes.stream().forEach(subtypeobj -> {
//                Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有",null);
//                items.put(subtypeobj.getId(),pagerGoods);
//            });
//        }

        return R.ok(activitiesVo);
    }

    /**
     * 特惠推荐-商品列表-小程序使用
     *
     * @return 特惠推荐信息
     */
    @GetMapping("/hot/preference")
    public R<ActivitiesV8Vo> getPreferenceMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesV8Vo activitiesVo = new ActivitiesV8Vo();
        activitiesVo.setTitle("特惠推荐");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesV8Vo>();

        ActivitiesV8Vo subActivitiesVoOne = new ActivitiesV8Vo();
        subActivitiesVoOne.setTitle("抢先尝鲜");
//            var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
//            if (CollUtil.isNotEmpty(subOnebanners)) {
//                subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
//            }
//        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setId("912000341");
        subActivitiesVoOne.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("912000341")) {
            subActivitiesVoOne.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoOne);


        ActivitiesV8Vo subActivitiesVoTwo = new ActivitiesV8Vo();
        subActivitiesVoTwo.setTitle("新品预告");
//            var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
//            if (CollUtil.isNotEmpty(subTwobanners)) {
//                subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
//            }
//        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setId("912000342");
        subActivitiesVoTwo.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("912000342")) {
            subActivitiesVoTwo.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoTwo);


        activitiesVo.setSubTypes(subTypes);

//        if(StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(),subActivitiesVoTwo.getId())){
//            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
//        }
//        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
//        if (StringUtils.isNotBlank(subType)) {
//            Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有",null);
//            items.put(subType, pagerGoods);
//        } else {
//            subTypes.stream().forEach(subtypeobj -> {
//                Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有",null);
//                items.put(subtypeobj.getId(), pagerGoods);
//            });
//        }
//        activitiesVo.setGoodsItems(items);
        return R.ok(activitiesVo);
    }


    /**
     * 新鲜好物-商品列表-小程序使用
     *
     * @return 新鲜好物信息
     */
    @GetMapping("/hot/new")
    public R<List<HotMutliVo>> getNewMutli(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "subType", required = false) String subType) {
        ActivitiesV8Vo activitiesVo = new ActivitiesV8Vo();
        activitiesVo.setTitle("新鲜好物");
        activitiesVo.setId(getMockId(activitiesVo.getTitle()));
        var banners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(activitiesVo.getTitle()));
        if (CollUtil.isNotEmpty(banners)) {
            activitiesVo.setBannerPicture(banners.get(0).getImgUrl());
        }
        activitiesVo.setSummary(null);
        var subTypes = new ArrayList<ActivitiesV8Vo>();

        ActivitiesV8Vo subActivitiesVoOne = new ActivitiesV8Vo();
        subActivitiesVoOne.setTitle("抢先尝鲜");
//            var subOnebanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoOne.getTitle()));
//            if (CollUtil.isNotEmpty(subOnebanners)) {
//                subActivitiesVoOne.setBannerPicture(subOnebanners.get(0).getImgUrl());
//            }
//        subActivitiesVoOne.setId(getMockId(subActivitiesVoOne.getTitle()));
        subActivitiesVoOne.setId("642000231");
        subActivitiesVoOne.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("642000231")) {
            subActivitiesVoOne.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoOne);

        ActivitiesV8Vo subActivitiesVoTwo = new ActivitiesV8Vo();
        subActivitiesVoTwo.setTitle("新品预告");
//            var subTwobanners = businessAdService.findActivityBanner(getShowClient(), BusinessAdStatic.DISTRIBUTION_SITE_ACTIVITY, getMockId(subActivitiesVoTwo.getTitle()));
//            if (CollUtil.isNotEmpty(subTwobanners)) {
//                subActivitiesVoTwo.setBannerPicture(subTwobanners.get(0).getImgUrl());
//            }
//        subActivitiesVoTwo.setId(getMockId(subActivitiesVoTwo.getTitle()));
        subActivitiesVoTwo.setId("642000232");
        subActivitiesVoTwo.setSummary(null);
        if (StringUtils.isBlank(subType) || subType.equals("642000232")) {
            subActivitiesVoTwo.setGoodsItems(getPageGoodsByEsGoods(page, pageSize, "自有", null));
        }
        subTypes.add(subActivitiesVoTwo);


        activitiesVo.setSubTypes(subTypes);

//        if(StringUtils.isNotBlank(subType) && !StringUtils.equalsAny(subType, subActivitiesVoOne.getId(),subActivitiesVoTwo.getId())){
//            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
//        }
//        Map<String, Pager<GoodsItemResultVo>> items = new HashMap<>();
//        if (StringUtils.isNotBlank(subType)) {
//            Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有","publishTime");
//            items.put(subType, pagerGoods);
//
//        } else {
//            subTypes.stream().forEach(subtypeobj -> {
//                Pager<GoodsItemResultVo> pagerGoods =  getPageGoodsByEsGoods(page,pageSize,"自有","publishTime");
//                items.put(subtypeobj.getId(), pagerGoods);
//            });
//        }
//
//        activitiesVo.setGoodsItems(items);
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
//        if(StringUtils.isNotBlank(keyword)){
//            queryVo.setKeyword(keyword);
//        }
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


