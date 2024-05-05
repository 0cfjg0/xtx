package com.itheima.xiaotuxian.vo.order;

import com.itheima.xiaotuxian.vo.member.CartVo;
import lombok.Data;

import java.util.List;

@Data
public class OrderSaveVo {
    /**
     * 商品集合
     */
    private List<CartVo> goods;
    /***
     * 所选地址Id
     */
    private String addressId;
    /**
     * 配送时间类型，1为不限，2为工作日，3为双休或假日
     */
    private Integer deliveryTimeType = 1;
    /**
     * 支付方式，1为在线支付，2为货到付款
     */
    private Integer payType = 1;
    /**
     * 买家留言
     */
    private String buyerMessage;
    /**
     * 用户Id
     */
    private String memberId;
    /**
     * 客户端标识
     */
    private String client;
    /**
     * TODO 支付渠道，1支付宝、2微信
     */
    private Integer payChannel = 1;
}
