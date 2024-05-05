package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuIndexVo {
    /**
     * skuId
     */
    private String id;
    /**
     * 可销售库存
     */
    private Integer saleableInventory;
    /**
     * 市场价
     */
    private BigDecimal marketPrice;
    /**
     * 售价
     */
    private BigDecimal sellingPrice;
    /**
     * skuCode
     */
    private String skuCode;
}
