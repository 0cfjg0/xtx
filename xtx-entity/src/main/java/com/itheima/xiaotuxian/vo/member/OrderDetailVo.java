package com.itheima.xiaotuxian.vo.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: itheima
 * @Date: 2020/11/3 10:20 上午
 * @Description:
 */
@Data
public class OrderDetailVo {
    private String id;
    /**
     * 下单时间
     */
    private LocalDateTime createTime;
    /**
     * 支付方式，1为在线支付，2为货到付款
     */
    private Integer payType;
    /**
     * 订单状态，1为待付款、2为待发货、3为待收货、4为待评价、5为已完成、6为已取消
     */
    private Integer orderState;
    /**
     * 付款截止时间
     */
    private LocalDateTime payLatestTime;
    /**
     * 计算倒计时--剩余的秒数 -1 表示已经超时，正数表示倒计时未结束
     */
    private Long countdown;
    /**
     * 邮费
     */
    private BigDecimal postFee;
    /**
     * 实付金额
     */
    private BigDecimal payMoney;
    /**
     * 支付渠道，1支付宝、2微信
     */
    private Integer payChannel;

    private Integer payState;
    /**
     * 金额合计
     */
    private BigDecimal totalMoney;
    /**
     * 数量合计
     */
    private Long totalNum;
    /**
     * 配送时间类型，1为不限，2为工作日，3为双休或假日
     */
    private Integer deliveryTimeType;
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
     * 付款时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime payTime;
    /**
     * 发货时间
     */
//    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime consignTime;
    /**
     * 交易完成时间
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
     * 商品集合
     */
    private List<OrderSkuVo> skus;

    /**
     * 预计到货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime arrivalEstimatedTime;
}
