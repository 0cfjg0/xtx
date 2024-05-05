package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-最小存货单元
 */
@Data
@TableName(value = "goods_sku_property_value")
public class GoodsSkuPropertyValue {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * skuId
     */
    private String skuId;
    /**
     * 属性组id
     */
    private String propertyGroupId;
    /**
     * 属性组名称
     */
    private String propertyGroupName;
    /**
     * 属性id
     */
    private String propertyMainId;
    /**
     * 关联属性名称
     */
    private String propertyMainName;
    /**
     * 属性值Id
     */
    private String propertyValueId;
    /**
     * 关联属性值名称
     */
    private String propertyValueName;
    /**
     * 关联属性值备注
     */
    private String propertyValueDescription;
    /**
     * 关联属性值图片id
     */
    private String propertyValuePictureId;
    /**
     * 父属性值名称
     */
    private String propertyParentValueName;

    /**
     * 商品的spuId
     */
    private String spuId;

    /**
     * 创建时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
