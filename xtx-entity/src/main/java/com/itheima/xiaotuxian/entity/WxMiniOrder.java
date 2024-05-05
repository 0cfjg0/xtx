package com.itheima.xiaotuxian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lenovo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxMiniOrder implements Serializable {

    private static final long serialVersionUID = 2310688218645441222L;
    /**订单表id*/
    private String orderId;
    /**订单号*/
    private String orderNum;
    /**用户openid*/
    private String openid;
    /**支付金额*/
    private double money;

}