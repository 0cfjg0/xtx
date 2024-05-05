package com.itheima.xiaotuxian.entity.classification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 类目-后台类目
 */
@Data
@TableName(value = "classification_backend")
public class ClassificationBackend extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 状态，0为正常，1为停用
     */
    private Integer state;
    /**
     * 层级，从1开始
     */
    private Integer layer;
    /**
     * 父级id
     */
    private String pid;
}
