package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "order_order")
public class Order extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 数量合计
     */
    private Long totalNum;
    /**
     * 金额合计
     */
    private BigDecimal totalMoney;
    /**
     * 优惠金额
     */
    private BigDecimal preMoney;
    /**
     * 邮费
     */
    private BigDecimal postFee;
    /**
     * 实付金额
     */
    private BigDecimal payMoney;
    /**
     * 支付方式，1为在线支付，2为货到付款
     */
    private Integer payType;
    /**
     * 付款截止时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime payLatestTime;
    /**
     * 付款时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime payTime;
    /**
     * 支付渠道，1支付宝、2微信
     */
    private Integer payChannel;
    /**
     * 发货时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime consignTime;
    /**
     * 交易完成时间(收货时间)
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endTime;
    /**
     * 交易关闭时间
     */
//    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime closeTime;
    /**
     * 完成评价时间
     */
//    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime evaluationTime;
    /**
     * 物流名称
     */
    private String shippingName;
    /**
     * 物流单号
     */
    private String shippingCode;
    /**
     * 买家留言
     */
    private String buyerMessage;
    /**
     * 是否评价,0为否，1为是
     */
    private Boolean buyerRate;
    /**
     * 收货人
     */
    private String receiverContact;
    /**
     * 收货人手机
     */
    private String receiverMobile;
    /**
     * 所在省份编码
     */
    private String provinceCode;
    /**
     * 所在城市编码
     */
    private String cityCode;
    /**
     * 所在区/县编码
     */
    private String countyCode;
    /**
     * 收货人地址
     */
    private String receiverAddress;
    /**
     * 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
     */
    private Integer sourceType;
    /**
     * 结算流水号
     */
    private String transactionId;
    /**
     * 支付状态，1为待支付，2为已支付，3为支付超时
     */
    private Integer payState;
    /**
     * 订单状态，1为待付款、2为待发货、3为待收货、4为待评价、5为已完成、6为已取消
     */
    private Integer orderState;
    /**
     * 发货状态，1为待发货，2为已发货，3为运输中，4为配送中，5为已收货
     */
    private Integer consignStatus;
    /**
     * 配送时间类型，1为不限，2为工作日，3为双休或假日
     */
    private Integer deliveryTimeType;
    /**
     * 取消理由
     */
    private String cancelReason;

    /**
     * 预计到货时间
     */
    private LocalDateTime arrivalEstimatedTime;

    /**
     * 退款金额
     */
    private BigDecimal refund;

    /**
     * 是否有退款
     */
    private String isRefund;

}
