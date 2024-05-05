package com.itheima.xiaotuxian.client;

import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.RefundRecord;
import com.itheima.xiaotuxian.vo.RefundRecordVo;

/**
 * @ClassName CommonPayHandler.java
 * @Description 基础交易处理接口
 */
public interface CommonPayHandler {

    /***
     * @description 统一收单线下交易查询
     * 该接口提供所有支付订单的查询，商户可以通过该接口主动查询订单状态，完成下一步的业务逻辑。
     * @param order 交易单
     * @return
     */
    Order queryTrading(Order order);

    /***
     * @description 统一收单交易退款接口
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，
     * 将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * @param record 交易退款单
     * @return
     */
    RefundRecord refundTrading(RefundRecordVo record);

    /***
     * @description 统一收单交易退款查询接口
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，
     * 将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * @param refundRecord 退款交易单
     * @return
     */
    RefundRecord queryRefundTrading(RefundRecord refundRecord) ;

    /***
     * @description 统一关闭订单
     * 1、商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
     * 2、系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
     * @param order 交易单
     * @return
     */
    //Order closeTrading(Order order);

    /***
     * @description 为方便商户快速查账，支持商户通过本接口获取商户离线账单下载地址
     * @param order 退款交易单
     * @return
     */
    Order downLoadBill(Order order);
}
