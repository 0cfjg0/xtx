package com.itheima.xiaotuxian.constant.statics;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:28
 * @Descripttion:
 */

import java.util.Arrays;
import java.util.List;

public class OrderStatic {
    private OrderStatic() {
    }

    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_PENDING_PAYMENT = 1;
    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_PENDING_DELIVERY = 2;
    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_PENDING_RECEIPT = 3;
    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_PENDING_EVALUATION = 4;
    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_FINISH = 5;
    /**
     * 订单状态：1、待付款2、待发货3、待收货4、待评价5、已完成6、已取消
     */
    public static final Integer STATE_CANCEL = 6;

    /**
     * 支付状态
     * 1、在线支付 2、货到付款
     */
    public static final Integer PAY_TYPE_ONLINE = 1;
    /**
     * 支付状态
     * 1、在线支付 2、货到付款
     */
    public static final Integer PAY_TYPE_RECEIVE = 2;

    /**
     * 支付渠道1、支付宝2、微信
     */
    public static final Integer PAY_CHANNEL_ALI = 1;
    /**
     * 支付渠道1、支付宝2、微信
     */
    public static final Integer PAY_CHANNEL_WEICHAT = 2;

    /**
     * PAY_STATE_PENDING 1
     * 支付状态1、待支付2、已支付3、支付超时4、支付失败
     *
     */
    public static final Integer PAY_STATE_PENDING = 1;
    /**
     * 支付状态1、待支付2、已支付3、支付超时4、支付失败
     */
    public static final Integer PAY_STATE_PAID = 2;
    /**
     * 支付状态1、待支付2、已支付3、支付超时4、支付失败
     */
    public static final Integer PAY_STATE_OVERTIME = 3;
    /**
     * 支付状态1、待支付2、已支付3、支付超时4、支付失败
     */
    public static final Integer PAY_STATE_FAIL = 4;

    /**
     * 发货状态1、待发货2、已发货3、运输中4、配送中5、已收货
     */
    public static final Integer CONSIGN_STATUS_SEND_PENDING = 1;
    /**
     * 发货状态1、待发货2、已发货3、运输中4、配送中5、已收货
     */
    public static final Integer CONSIGN_STATUS_SEND = 2;
    /**
     * 发货状态1、待发货2、已发货3、运输中4、配送中5、已收货
     */
    public static final Integer CONSIGN_STATUS_TRANSPORTING = 3;
    /**
     * 发货状态1、待发货2、已发货3、运输中4、配送中5、已收货
     */
    public static final Integer CONSIGN_STATUS_DELIVERY = 4;
    /**
     * 发货状态1、待发货2、已发货3、运输中4、配送中5、已收货
     */
    public static final Integer CONSIGN_STATUS_RECEIVED = 5;


    /**
     * 退款状态  1 待退款、  2 退款中、3 退款成功、4 退款失败
     */
    public static final Integer WAIT_REFUND = 1;
    public static final Integer SEDING_REFUND = 2;
    public static final Integer SUCCESS_REFUND = 3;
    public static final Integer FAIL_REFUND = 4;


    /**
     * 配送时间类型
     */
    // 不限
    public static final Integer DELIVERY_TIME_TYPE_ANY = 1;
    // 工作日
    public static final Integer DELIVERY_TIME_TYPE_WORKDAY = 2;
    // 双休或假日
    public static final Integer DELIVERY_TIME_TYPE_WEEKEND = 3;

    /**
     * 订单来源
     */
    // web
    public static final Integer SOURCE_TYPE_WEB = 1;
    // app
    public static final Integer SOURCE_TYPE_APP = 2;
    // 小程序
    public static final Integer SOURCE_TYPE_WXMINI = 3;

    // 预定义 评价标签
    private static final String[] EVALUATE_TAGS = new String[] { "质量上乘" };

    public static List<String> getEvaluateTags() {
        return Arrays.asList(EVALUATE_TAGS);
    }
}
