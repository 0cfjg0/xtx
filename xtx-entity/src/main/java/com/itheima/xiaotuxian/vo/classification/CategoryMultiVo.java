package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

import java.util.List;

@Data
public class CategoryMultiVo {
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
     * 轮播图
     */
    private List<ImageBannerVo> imageBanners;
    /**
     * 子类集合
     */
    private List<SubCategoryVo> children;
}
