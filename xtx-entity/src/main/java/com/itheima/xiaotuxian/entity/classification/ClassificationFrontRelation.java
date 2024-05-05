package com.itheima.xiaotuxian.entity.classification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "classification_front_relation")
public class ClassificationFrontRelation extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
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
