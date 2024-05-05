package com.itheima.xiaotuxian.vo.classification;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:30
 * @Descripttion: 
 */

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;

import lombok.Data;

@Data
public class CategoryVo {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String picture;
    /**
     * 子类集合
     */
    private List<SubCategoryVo> children;
    /**
     * @description: app使用的banner图
     * @param {*}
     * @return {*}
     * @author: lbc
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BannerResultVo> banners;
}
