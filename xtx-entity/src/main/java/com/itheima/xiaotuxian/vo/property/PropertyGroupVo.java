package com.itheima.xiaotuxian.vo.property;

import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:23 下午
 * @Description:
 */
@Data
public class PropertyGroupVo {
    private String id;
    /**
     * 属性组名称
     */
    private String name;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    private Integer propertyType;
    /**
     * 属性组状态，0为启用，1为禁用
     */
    private Integer state;
    /**
     * 属性组别名
     */
    private String alias;
    /**
     * 属性集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertySimpleVo> properties;
}
