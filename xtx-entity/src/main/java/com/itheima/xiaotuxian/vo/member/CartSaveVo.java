package com.itheima.xiaotuxian.vo.member;

import lombok.Data;
import org.apache.ibatis.annotations.Param;


@Data
public class CartSaveVo {
    /**
     * 商品id
     * sluId
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
