package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

@Data
public class OrderBuildVo {
    /**
     * sku id 集合
     */
    private String skuId;
    /**
     * 立即购买的数量
     */
    private Integer count;
    /**
     * 立即购买选择的地址
     */
    private String addressId;
}
