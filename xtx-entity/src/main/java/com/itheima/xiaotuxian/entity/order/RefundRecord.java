package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description：退款记录表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("order_refund_record")
@ApiModel(value="RefundRecord对象", description="退款记录表")
public class RefundRecord extends AbstractBasePO {

    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "业务系统订单号")
    private String productOrderNo;

    @ApiModelProperty(value = "本次退款订单号")
    private String refundNo;


    @ApiModelProperty(value = "退款渠道【支付宝、微信、现金】")
    private Integer tradingChannel;

    @ApiModelProperty(value = "退款状态 1 待退款、 2 退款中、3 退款成功、4 退款失败")
    private Integer refundStatus;

    @ApiModelProperty(value = "返回编码")
    private String refundCode;

    @ApiModelProperty(value = "返回信息")
    private String refundMsg;

    @ApiModelProperty(value = "备注【订单门店，桌台信息】")
    private String memo;

    @ApiModelProperty(value = "本次退款金额")
    private BigDecimal refundAmount;


}
