package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.order.RefundRecord;
import com.itheima.xiaotuxian.mapper.order.RefundRecordMapper;
import com.itheima.xiaotuxian.service.order.RefundRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description： 退款记录服务实现类
 */
@Service
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundRecordService {

    @Override
    public List<RefundRecord> findByTradingState(String tradingState) {
        QueryWrapper<RefundRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(RefundRecord::getRefundStatus, tradingState);
        return list(queryWrapper);
    }

    @Override
    public RefundRecord findRefundRecordByProductOrderNoAndSending(String productOrderNo, Integer status) {
        QueryWrapper<RefundRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(RefundRecord::getProductOrderNo, productOrderNo)
                .eq(RefundRecord::getRefundStatus, status);
        return getOne(queryWrapper);
    }
}
