package com.itheima.xiaotuxian.vo.goods;

import lombok.Data;

@Data
public class AttributionVo {
    /**
     * 数据Id
     */
    private String id;
    /**
     * 归属id，例如父级分类id或者所属分类id
     */
    private String relegationId;
    /**
     * 数据名称
     */
    private String name;
    /**
     * 数据英文名称
     */
    private String nameEn;
    /**
     * Logo链接
     */
    private String logo;
    /**
     * 数据类型，backend为后台分类，brand为品牌
     */
    private String dataType;
}
