package com.itheima.xiaotuxian.vo;

import com.itheima.xiaotuxian.entity.AbstractBasePO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description：
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RefundRecordVo对象", description="")
public class RefundRecordVo extends AbstractBasePO {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "业务系统订单号")
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    private String productOrderNo;

    @ApiModelProperty(value = "本次退款订单号")
    private String refundNo;
    @ApiModelProperty(value = "本次退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "退款渠道【支付宝、微信、现金】")
    private String tradingChannel;

    @ApiModelProperty(value = "退款状态 1 待退款、 2 退款中、3 退款成功、4 退款失败")
    private Integer refundStatus;

    @ApiModelProperty(value = "返回编码")
    private String refundCode;

    @ApiModelProperty(value = "返回信息")
    private String refundMsg;

    @ApiModelProperty(value = "备注")
    private String memo;

}
