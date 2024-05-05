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
public class OrderPageVo {
    private String id;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
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
     * 邮费
     */
    private BigDecimal postFee;
    /**
     * 实付金额
     */
    private BigDecimal payMoney;
    /**
     * 金额合计
     */
    private BigDecimal totalMoney;
    /**
     * 数量合计
     */
    private Long totalNum;
    /**
     * 商品集合
     */
    private List<OrderSkuVo> skus;
    /**
     * 支付渠道，1支付宝、2微信
     */
    private Integer payChannel;
    /**
     * 计算倒计时--剩余的秒数 -1 表示已经超时，正数表示倒计时未结束
     */
    private Long countdown;
    /**
     * 退款金额
     */
    private BigDecimal refund;

    /**
     * 是否有退款
     */
    private String isRefund;
 }
