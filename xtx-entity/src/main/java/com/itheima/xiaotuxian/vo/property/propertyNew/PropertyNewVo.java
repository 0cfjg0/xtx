package com.itheima.xiaotuxian.vo.property.propertyNew;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:59 下午
 * @Description:
 */
@Data
public class PropertyNewVo {
    private String id;

    /**
     * 属性名称
     */
    private String name;
    /**
     * 属性别名
     */
    private String alias;

    /**
     * 是否备注，0为备注，1为不备注
     */
    private Integer isRemark;

    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    private Integer propertyType;
    /**
     * 状态,0为启用，1为禁用
     */
    private Integer state;
    /**
     * 值和子值是否传图，0为否，1为是
     */
    private Integer valueHasPicture;

    /**
     * 属性组id
     */
    private PropertyGroupNewVo group;

    /**
     * 属性值列表
     */
    private List<PropertyValueNewVo> values;
}
