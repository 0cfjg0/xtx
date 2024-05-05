package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

@Data
public class EsPropertyValue {
    /**
     * 属性组名称
     */
    private String propertyGroupName;
    /**
     * 属性名称
     */
    private String propertyMainName;
    /**
     * 属性值
     */
    private String propertyValueName;
}
