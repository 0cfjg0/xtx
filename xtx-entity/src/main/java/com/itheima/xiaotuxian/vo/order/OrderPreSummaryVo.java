package com.itheima.xiaotuxian.vo.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreSummaryVo {
    /**
     * 商品件数
     */
    private Integer goodsCount;
    /**
     * 价格总计
     */
    private BigDecimal totalPrice;
    /**
     * 应付总计
     */
    private BigDecimal totalPayPrice;
    /**
     * 邮费
     */
    private BigDecimal postFee;
    /**
     * 折扣总计
     */
    private BigDecimal discountPrice;
}
