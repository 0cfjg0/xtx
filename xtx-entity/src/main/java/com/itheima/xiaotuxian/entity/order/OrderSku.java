package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "order_order_sku")
public class OrderSku {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 商品id
     */
    private String skuId;
    /**
     * spu id
     */
    private String spuId;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 图片地址
     */
    private String image;
    /**
     * 当前价格
     */
    private BigDecimal curPrice;
    /**
     * 实付金额
     */
    private BigDecimal realPay;
    /**
     * 商品名称
     */
    private String name;
}
