package com.itheima.xiaotuxian.entity.property;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 属性-属性组
 */
@Data
@TableName(value = "property_group")
public class PropertyGroup extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
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
     * 别名
     */
    private String alias;
}
