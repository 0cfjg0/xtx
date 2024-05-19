package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.order.OrderEvaluateVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 手机商品详情页面返回信息
 * 和PC的不同
 * add on 2023-03-23 by lbencai
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class SaleGoodsAppDetailVo {
    /**
     * id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * 备注
     */
    private String desc;
    /**
     * 当前价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 原价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal oldPrice;
    /**
     * 折扣信息，当折扣信息大于0时有效
     */
    private BigDecimal discount;
    /**
     * 库存
     */
    private Integer inventory;
    /**
     * 品牌信息
     */
    private BrandSimpleVo brand;
    /**
     * 主图视频集合
     */
    private List<String> mainVideos;
    /**
     * 主图视频比例,1为1:1/16:9，2为3:4
     */
    private Integer videoScale;
    /**
     * 主图图片集合
     */
    private List<String> mainPictures;
    /**
     * 可选规格集合
     */
    private List<GoodsSpecItemVo> specs;
    /**
     * sku集合
     */
    private List<SkuVo> skus;
    /**
     * 商品详情
     */
    private SpuDetailsVo details;

    /**
     * 是否为预售商品
     */
    private Boolean isPreSale;
    /**
     * 是否已收藏
     */
    private Boolean isCollect;
    /**
     * 推荐商品集合，仅APP有此数据
     */
    private List<GoodsItemResultVo> recommends;
    /**
     * 用户地址集合
     */
    private List<AddressSimpleVo> userAddresses;
    /**
     * 同类商品
     */
    private List<GoodsItemResultVo> similarProducts;
    /**
     * 24小时热销
     */
    private List<GoodsItemResultVo> hotByDay;
    /**
     * 评价信息
     */
    private OrderEvaluateVo evaluationInfo;
}