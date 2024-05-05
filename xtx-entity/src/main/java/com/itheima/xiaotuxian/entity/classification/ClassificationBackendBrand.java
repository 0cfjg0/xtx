package com.itheima.xiaotuxian.entity.classification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 类目-后台类目与品牌关联
 */
@Data
@TableName(value = "classification_backend_brand")
public class ClassificationBackendBrand extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 后台分类id
     */
    private String classificationBackendId;
    /**
     * 品牌id
     */
    private String brandId;
}
