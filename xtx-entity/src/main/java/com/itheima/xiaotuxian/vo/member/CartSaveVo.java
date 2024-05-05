package com.itheima.xiaotuxian.vo.member;

import lombok.Data;


@Data
public class CartSaveVo {
    /**
     * SKUID
     */
    private String skuId;
    /**
     * 是否选中
     */
    private Boolean selected;
    /**
     * 数量
     */
    private Integer count;
}
