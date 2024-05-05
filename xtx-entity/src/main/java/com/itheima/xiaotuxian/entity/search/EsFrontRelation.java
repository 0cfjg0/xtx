package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

@Data
public class EsFrontRelation {
    private String id;
    /**
     * 前台类目id
     */
    private String frontId;
    /**
     * 关联标识
     */
    private String relationKey;
    /**
     * 关联对象id
     */
    private String objectId;
    /**
     * 关联数据类型，1为后台类目，2为销售属性组，3为品牌
     */
    private Integer objectType;
}
