package com.itheima.xiaotuxian.vo.goods.brand;

import lombok.Data;

@Data
public class BrandSimpleVo {
    /**
     * 品牌Id
     */
    private String id;
    /**
     * 品牌名称
     */
    private String name;
    /**
     * 英文品牌名
     */
    private String nameEn;
    /**
     * 品牌logo
     */
    private String logo;
    /**
     * 图片
     */
    private String picture;
    /**
     * 属性来源：1为本级，2为父级
     */
    private Integer type;

    /**
     * 描述
     */
    private String desc;
    /**
     * 地址信息
     */
    private String place;
}
