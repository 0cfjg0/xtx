package com.itheima.xiaotuxian.entity.classification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 类目-后台类目与属性组关联
 */
@Data
@TableName(value = "classification_backend_property_group")
public class ClassificationBackendPropertyGroup extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 后台分类id
     */
    private String classificationBackendId;
    /**
     * 属性组id
     */
    private String propertyGroupId;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    private Integer propertyType;
}
