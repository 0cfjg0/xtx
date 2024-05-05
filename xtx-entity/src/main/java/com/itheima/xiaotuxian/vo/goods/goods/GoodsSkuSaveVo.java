package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsSkuSaveVo {
    /**
     * id
     */
    private String id;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 可销售库存
     */
    private Integer saleableInventory;
    /**
     * 实际库存
     */
    private Integer physicalInventory;
    /**
     * 市场价
     */
    private BigDecimal marketPrice;
    /**
     * 售价
     */
    private BigDecimal sellingPrice;
    /**
     * 属性值集合
     */
    private List<GoodsPropertyValueVo> propertyValues;
}
