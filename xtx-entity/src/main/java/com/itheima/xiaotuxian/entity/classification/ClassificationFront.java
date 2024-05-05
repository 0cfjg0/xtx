package com.itheima.xiaotuxian.entity.classification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 类目-前台类目
 */
@Data
@TableName(value = "classification_front")
public class ClassificationFront extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 前台分类名称
     */
    private String name;
    /**
     * 分类图片id
     */
    private String pictureId;
    /**
     * 父级分类id
     */
    private String pid;
    /**
     * 根分类id
     */
    private String rootId;
    /**
     * 层级，从1开始
     */
    private Integer layer;
    /**
     * 排序，从1开始，数值越大越靠前
     */
    private Integer sort;
    /**
     * 是否启用，0为启用、1为未启用
     */
    private Integer state;
}
