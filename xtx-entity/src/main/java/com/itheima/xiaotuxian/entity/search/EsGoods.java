package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EsGoods implements Serializable{
    /**
     * 商品id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 后台类目信息
     */
    private EsBackend backend;
    /**
     * 品牌信息
     */
    private EsBrand brand;
    /**
     * 前台类目集合
     */
    private List<EsFront> fronts;
    /**
     * PC的描述
     */
    private String pcDecription;
    /**
     * APP的描述
     */
    private String appDecription;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * 销售属性集合，多值以半角空格分隔
     */
    private String sales;
    /**
     * 状态：1为出售中、3为已售罄
     */
    private Integer state;
    /**
     * 发布时间
     */
    private Long publishTime;
    /**
     * 前台关联关系
     */
    private List<EsFrontRelation> frontRelations;
    /**
     * 商品销售属性
     */
    private List<EsPropertyValue> saleProperties;
    /**
     * 订单数
     */
    private Long orderNum;
    /**
     * 评论数
     */
    private Long evaluateNum;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * pc商品图片
     */
    private String pcPicture;
    /**
     * app商品图片
     */
    private String appPicture;
    /**
     * 价格
     */
    private BigDecimal discountPrice;
    /**
     * 是否有折扣
     */
    private Boolean hasDiscount;
    /**
     * 折扣信息
     */
    private BigDecimal discount;
    /**
     * 所配置的关键词集合
     */
    private List<String> hotKeys;
    /**
     * 库存
     */
    private Integer inventory;
}
