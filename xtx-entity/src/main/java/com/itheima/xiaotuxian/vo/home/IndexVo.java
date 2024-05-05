package com.itheima.xiaotuxian.vo.home;

import com.itheima.xiaotuxian.entity.marketing.MarketingTopic;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;
import com.itheima.xiaotuxian.vo.home.response.RecommendVo;
import lombok.Data;

import java.util.List;

/**
 * 一次拉取首页所有数据-app
 * @author zsf
 */
@Data
public class IndexVo {
    /**
     * 轮播图列表
     */
    private List<BannerResultVo> imageBanners;
    /**
     * 分类列表
     */
    private List<FrontResultVo> categoryGrids;
    /**
     * 推荐列表
     */
    private List<RecommendVo> hotRecommends;
    /**
     * 新鲜好物
     */
    private List<GoodsItemResultVo> freshGoods;
    /**
     * 热门品牌
     */
    private List<BrandSimpleVo> hotBrands;
    /**
     * 专题推荐
     */
    private List<MarketingTopic> projects;
    /**
     * 商品推荐分类列表
     */
    private List<FrontResultVo> categoryBanners;
}
