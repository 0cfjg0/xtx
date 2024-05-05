package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-spu属性
 */
@Data
@TableName(value = "goods_spu_property")
public class GoodsSpuProperty {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 属性组名称
     */
    private String propertyGroupName;
    /**
     * 属性组id
     */
    private String propertyGroupId;
    /**
     * 关联属性名称
     */
    private String propertyMainName;
    /**
     * 值名称
     */
    private String propertyValueName;
    /**
     * spuid
     */
    private String spuId;
    /**
     * 创建时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
}
