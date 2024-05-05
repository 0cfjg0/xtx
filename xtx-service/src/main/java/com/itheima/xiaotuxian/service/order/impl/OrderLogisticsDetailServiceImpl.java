package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.order.OrderLogisticsDetail;
import com.itheima.xiaotuxian.mapper.order.OrderLogisticsDetailMapper;
import com.itheima.xiaotuxian.service.order.OrderLogisticsDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class OrderLogisticsDetailServiceImpl extends ServiceImpl<OrderLogisticsDetailMapper, OrderLogisticsDetail> implements OrderLogisticsDetailService {
    @Resource
    OrderLogisticsDetailMapper orderLogisticsDetailMapper;
    @Override
    public List<OrderLogisticsDetail> selectList(String logisticsId) {
        return orderLogisticsDetailMapper.selectList(Wrappers.<OrderLogisticsDetail>lambdaQuery()
                .eq(OrderLogisticsDetail::getOrderLogisticsId, logisticsId)
                .orderByDesc(OrderLogisticsDetail::getLogisticsTime,OrderLogisticsDetail::getId));
    }
}
