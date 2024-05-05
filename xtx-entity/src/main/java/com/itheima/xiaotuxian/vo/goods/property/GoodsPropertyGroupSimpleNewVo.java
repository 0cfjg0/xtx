package com.itheima.xiaotuxian.vo.goods.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPropertyGroupSimpleNewVo {
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
    @JsonIgnore
    private Integer propertyType;

    /**
     * 属性集合
     */
    private List<GoodsPropertyNewVo> properties;
    /**
     * 属性来源：1为本级，2为父级
     */
    @JsonIgnore
    private Integer type;
}
