package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.RefundRecord;

import java.util.List;

/**
 * @Description： 退款记录表服务类
 */
public interface RefundRecordService extends IService<RefundRecord> {

    /***
     * @description 按交易状态查询交易单
     * @param tradingState
     * @return
     */
    List<RefundRecord> findByTradingState(String tradingState);
    /***
     * @description 查询当前订单是否有退款中的记录
     *
     * @param productOrderNo
     * @return
     */
    RefundRecord findRefundRecordByProductOrderNoAndSending(String productOrderNo, Integer status);
}
