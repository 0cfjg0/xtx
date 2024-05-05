package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

@Data
public class GoodsSaleableInventoryVo {
    /**
     * skuId
     */
    private String id;
    /**
     * 可销售库存
     */
    private Integer saleableInventory;
}
