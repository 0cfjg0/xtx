package com.itheima.xiaotuxian.vo.property;

import lombok.Data;

import java.util.List;

@Data
public class PropertyGroupSimpleVo {
    /**
     * 属性组id
     */
    private String id;
    /**
     * 属性组名称
     */
    private String name;
    /**
     * 属性组类型
     */
    private Integer propertyType;

    /**
     * 属性集合
     */
    private List<PropertyVo> properties;
    /**
     * 属性来源：1为本级，2为父级
     */
    private Integer type;
}
