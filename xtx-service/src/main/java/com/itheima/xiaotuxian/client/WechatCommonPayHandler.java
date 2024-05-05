package com.itheima.xiaotuxian.client;

import com.alibaba.fastjson.JSONObject;
import com.itheima.xiaotuxian.client.wechat.Config;
import com.itheima.xiaotuxian.client.wechat.Factory;
import com.itheima.xiaotuxian.client.wechat.TradingConstant;
import com.itheima.xiaotuxian.client.wechat.TradingEnum;
import com.itheima.xiaotuxian.client.wechat.response.QueryResponse;
import com.itheima.xiaotuxian.client.wechat.response.RefundResponse;
import com.itheima.xiaotuxian.config.wechat.WechatPayConfig;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.RefundRecord;
import com.itheima.xiaotuxian.util.ResponseChecker;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @ClassName BasicPayHandlerImpl.java
 * @Description 微信交易基础类
 */
@Service
@Slf4j
public class WechatCommonPayHandler implements CommonPayHandler {

    @Autowired
    WechatPayConfig wechatPayConfig;

    @Override
    public Order queryTrading(Order order) {
        //1、获得微信客户端
        Config config = wechatPayConfig.config();
        //2、配置如果为空，抛出异常1
        if (config == null) {
            throw new RuntimeException(TradingEnum.CONFIG_ERROR.getMsg());
        }
        //3、指定配置文件
        Factory factory = new Factory();
        factory.setOptions(config);
        //4、调用接口
        try {
            QueryResponse queryResponse = factory.Common()
                    .query(String.valueOf(order.getId()));
            //5、判断响应是否成功
            boolean success = ResponseChecker.success(queryResponse);
            //6、响应成功，分析交易状态
            String tradeState = queryResponse.getTradeState();
            if (success && StringUtils.isNotBlank(tradeState)) {
                //SUCCESS：支付成功,REFUND：转入退款,NOTPAY：未支付,CLOSED：已关闭,REVOKED：已撤销（仅付款码支付会返回）
                //USERPAYING：用户支付中（仅付款码支付会返回）,PAYERROR：支付失败（仅付款码支付会返回）
                switch (queryResponse.getTradeState()) {
                    case TradingConstant.WECHAT_TRADE_CLOSED:
                        order.setPayState(TradingConstant.TRADE_CLOSED_VALUE);
                        order.setOrderState(TradingConstant.TRADE_CLOSED_VALUE);
                        break;
                    case TradingConstant.WECHAT_TRADE_REVOKED:
                        order.setPayState(TradingConstant.TRADE_CLOSED_VALUE);
                        order.setOrderState(TradingConstant.TRADE_CLOSED_VALUE);
                        break;
                    case TradingConstant.WECHAT_TRADE_SUCCESS:
                        order.setPayState(TradingConstant.TRADE_SUCCESS_VALUE);
                        order.setOrderState(TradingConstant.TRADE_SUCCESS_VALUE);
                        order.setPayTime(LocalDateTime.now());
                        break;
                    case TradingConstant.WECHAT_TRADE_REFUND:
                        order.setIsRefund(TradingConstant.WECHAT_TRADE_ISREFUND_NO);
                        order.setOrderState(TradingConstant.WECHAT_TRADE_REFUND_VALUE);
                        break;
                    default:
                        break;
                }

            } else {
                throw new RuntimeException("查询微信统一下单失败！");
            }
        } catch (Exception e) {
            log.warn("查询微信统一下单失败：{}", e.getMessage());
            throw new RuntimeException("查询微信统一下单失败！");
        }
        return order;
    }

    @Override
    public RefundRecord refundTrading(RefundRecordVo record) {
        //1、获得微信客户端
        Config config = wechatPayConfig.config();
        //2、配置如果为空，抛出异常
        if (config == null) {
            throw new RuntimeException(TradingEnum.CONFIG_ERROR.getMsg());
        }
        //3、指定配置文件
        Factory factory = new Factory();
        factory.setOptions(config);
        //4、调用接口
        try {
            RefundResponse refundResponse = factory.Common()
                .refund(String.valueOf(record.getProductOrderNo()),
                String.valueOf(record.getRefundMoney()),
                        record.getRefundNo(),String.valueOf(record.getPayMoney()));
            boolean success = ResponseChecker.success(refundResponse);
            if (success){
                //5、修改退款单信息
                record.setRefundCode(refundResponse.getCode() );
                record.setRefundMsg( refundResponse.getMessage() );
                switch (refundResponse.getStatus()){
                    case TradingConstant.WECHAT_REFUND_SUCCESS:
                        record.setRefundStatus(TradingConstant.REFUND_SUCCESS_VALUE);break;
                    case TradingConstant.WECHAT_REFUND_CLOSED:
                        record.setRefundStatus(TradingConstant.REFUND_CLOSED_VALUE);break;
                    case TradingConstant.WECHAT_REFUND_PROCESSING:
                        record.setRefundStatus(TradingConstant.REFUND_PROCESSING_VALUE);break;
                    default:
                        record.setRefundStatus(TradingConstant.REFUND_CLOSED_VALUE);break;
                }
            }else {
                log.error("网关：微信统一下单退款失败：{},结果：{}", record.getProductOrderNo(),
                        JSONObject.toJSONString(refundResponse));
                throw new RuntimeException("网关：微信统一下单退款失败!");
            }
        } catch (Exception e) {
            log.error("微信统一下单退款失败：{}", e.getMessage());
            throw new RuntimeException(TradingEnum.TRAD_REFUND_FAIL.getMsg());
        }
        // 6 vo->po
        RefundRecord recordResult = new RefundRecord();
        BeanUtils.copyProperties(record, recordResult);
        return recordResult;
    }

    @Override
    public RefundRecord queryRefundTrading(RefundRecord refundRecord) {
        //1、获得微信客户端
        Config config = wechatPayConfig.config();
        //2、配置如果为空，抛出异常
        if (config == null) {
            throw new RuntimeException(TradingEnum.CONFIG_ERROR.getMsg());
        }
        //3、指定配置文件
        Factory factory = new Factory();
        factory.setOptions(config);
        try {
            RefundResponse refundResponse = factory.Common().queryRefund(refundRecord.getRefundNo());
            //4、判断响应是否成功
            boolean success = ResponseChecker.success(refundResponse);
            //5、查询出的退款状态
            if (success&&TradingConstant.WECHAT_REFUND_SUCCESS.equals(refundResponse.getStatus())){
                refundRecord.setRefundStatus(TradingConstant.REFUND_SUCCESS_VALUE);
                refundRecord.setRefundCode(refundResponse.getCode());
                refundRecord.setRefundMsg(refundResponse.getMessage());
            }else {
                log.error("网关：查询微信统一下单退款失败：{},结果：{}", refundRecord.getRefundNo(),
                    JSONObject.toJSONString(refundResponse));
                throw new RuntimeException("网关：查询微信统一下单退款失败！");
            }
        } catch (Exception e) {
            log.warn("查询微信统一下单退款失败：{}", e.getMessage());
            throw new RuntimeException("查询微信统一下单退款失败！");
        }
        return refundRecord;
    }
    //
    //@Override
    //public Order closeTrading(Order Order) {
    //    //1、获得微信客户端
    //    Config config = wechatPayConfig.config();
    //    //2、配置如果为空，抛出异常
    //    if (EmptyUtil.isNullOrEmpty(config)){
    //        throw new ProjectException(TradingEnum.CONFIG_ERROR);
    //    }
    //    //3、使用配置
    //    Factory factory = new Factory();
    //    factory.setOptions(config);
    //    try {
    //        //4、调用三方API关闭订单
    //        CloseResponse closeResponse = factory.Common()
    //            .close(String.valueOf(Order.getTradingOrderNo()));
    //        //5、关闭订单受理情况
    //        boolean success = ResponseChecker.success(closeResponse);
    //        if (success){
    //            Order.setTradingState(TradingConstant.TRADE_CLOSED);
    //            return Order;
    //        }else {
    //            log.error("网关：微信关闭订单失败：{},结果：{}", Order.getTradingOrderNo(),
    //                JSONObject.toJSONString(closeResponse));
    //            throw  new RuntimeException("网关：微信关闭订单失败!");
    //        }
    //    } catch (Exception e) {
    //        log.warn("微信关闭订单失败：{}", ExceptionsUtil.getStackTraceAsString(e));
    //        throw  new ProjectException(TradingEnum.TRAD_CLOSE_FAIL);
    //    }
    //}

    @Override
    public Order downLoadBill(Order Order) {
        //toDo 后面补齐
        throw  new RuntimeException("未支持：微信关下载账单!");
    }


}

