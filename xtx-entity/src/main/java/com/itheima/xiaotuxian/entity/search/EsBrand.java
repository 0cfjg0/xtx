package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

@Data
public class EsBrand {
    /**
     * id
     */
    private String id;
    /**
     * 品牌名称
     */
    private String name;
    /**
     * 品牌英文名称
     */
    private String nameEn;
    /**
     * 产地-国家
     */
    private String productionPlaceCountry;
    /**
     * 产地-州、省
     */
    private String productionPlaceState;
}
