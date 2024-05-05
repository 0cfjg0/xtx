package com.itheima.xiaotuxian.vo.goods.goods;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 
 */
@Data
public class GoodsSpecValueVo {
    /**
     * 可选值名称
     */
    private String name;
    /**
     * 可选值图片链接
     */
    private String picture;
    /**
     * 是否可售
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean available;
    /**
     * 可选值备注
     */
    private String desc;
}
