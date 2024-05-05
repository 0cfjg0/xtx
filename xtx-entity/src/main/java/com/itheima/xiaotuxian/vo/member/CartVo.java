package com.itheima.xiaotuxian.vo.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import com.itheima.xiaotuxian.vo.goods.goods.SkuSpecVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class CartVo {
    /**
     * SPUID
     */
    private String id;
    /**
     * SKUID
     */
    private String skuId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 属性文字，例如“颜色:瓷白色 尺寸：8寸”
     * PC端传值
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String attrsText;
    /**
     * 规格信息集合
     * 手机端传值
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SkuSpecVo> specs;
    /**
     * 商品图片
     */
    private String picture;
    /**
     * 加入时价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 当前的价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal nowPrice;
    /**
     * 当前价格原价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal nowOriginalPrice;
    /**
     * 是否选中
     */
    private Boolean selected;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 是否为有效商品
     */
    private Boolean isEffective;
    /**
     * 折扣信息
     */
    private BigDecimal discount;
    /**
     * 是否收藏
     */
    private Boolean isCollect;
    /**
     * 运费
     */
    private BigDecimal postFee;
}
