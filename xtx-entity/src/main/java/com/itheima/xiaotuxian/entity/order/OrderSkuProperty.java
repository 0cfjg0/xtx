package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-最小存货单元
 */
@Data
@TableName(value = "order_order_sku_property")
public class OrderSkuProperty {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 订单sku id
     */
    private String orderSkuId;
    /**
     * 属性组id
     */
    private String propertyGroupId;
    /**
     * 属性组名称
     */
    private String propertyGroupName;
    /**
     * 关联属性id
     */
    private String propertyMainId;
    /**
     * 关联属性名称
     */
    private String propertyMainName;
    /**
     * 关联属性值id
     */
    private String propertyValueId;
    /**
     * 关联属性值名称
     */
    private String propertyValueName;
    /**
     * 关联属性值备注
     */
    private String propertyValueDecription;
    /**
     * 关联属性值图片id
     */
    private String propertyValuePictureId;
    /**
     * 关联属性值父id
     */
    private String propertyValueParentId;
    /**
     * 关联属性值父名称
     */
    private String propertyValueParentName;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单spu id
     */
    private String orderSpuId;
}
