package com.itheima.xiaotuxian.vo.goods;

import lombok.Data;

@Data
public class BrandVo {
    /**
     * id
     */
    private String id;
    /**
     * logo
     */
    private String logo;
    /**
     * 标语、宣传语
     */
    private String slogan;
    /**
     * 品牌名
     */
    private String name;
    /**
     * 英文品牌名
     */
    private String nameEn;
    /**
     * 产地
     */
    private String productionPlace;
    /**
     * 品牌大图
     */
    private String brandImage;
    /**
     * 首字母
     */
    private String firstWord;
    /**
     * 品牌故事
     */
    private String brandStory;
    /**
     * 关注数
     */
    private Integer collectNum;
    /**
     * 是否已关注
     */
    private Boolean isCollect;
}
