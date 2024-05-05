package com.itheima.xiaotuxian.vo.evaluate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class EvaluateGoodsVo {
    /**
     * spuId
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 图片
     */
    private String picture;
    /**
     * 数量
     */
    private Integer count;
    /**
     * SKUID
     */
    private String skuId;
    /**
     * 属性文字，例如“颜色:瓷白色 尺寸：8寸”
     */
    private String attrsText;
    /**
     * 原单价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 实付单价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal payPrice;
    /**
     * 小计总价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal totalPrice;
    /**
     * 实付价格小计
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal totalPayPrice;
    /**
     * 印象标签集合
     */
    private List<String> tags;
}
