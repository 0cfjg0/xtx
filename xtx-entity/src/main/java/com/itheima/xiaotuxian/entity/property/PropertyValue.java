package com.itheima.xiaotuxian.entity.property;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 属性-属性值
 */
@Data
@TableName(value = "property_value")
public class PropertyValue extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 父级属性值id
     */
    private String pid;
    /**
     * 值名称
     */
    private String valueName;
    /**
     * 值图片
     */
    private String valuePicture;
    /**
     * 属性Id
     */
    private String mainId;
    /**
     * 值备注
     * 2022年3月28日17:55:58增加
     */
    private String remark;

}
