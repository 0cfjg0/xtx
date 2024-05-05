package com.itheima.xiaotuxian.vo.goods;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuSimpleVo {
    /**
     * 现价
     */
    private BigDecimal nowPrice;
    /**
     * 原价
     */
    private BigDecimal oldPrice;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 折扣
     */
    private BigDecimal discount;
    /**
     * 是否有效状态
     */
    private Boolean isEffective;
}
