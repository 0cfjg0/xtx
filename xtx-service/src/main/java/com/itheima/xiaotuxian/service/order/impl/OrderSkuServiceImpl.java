package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.mapper.order.OrderSkuMapper;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderSkuServiceImpl extends ServiceImpl<OrderSkuMapper, OrderSku> implements OrderSkuService {

    @Autowired
    private OrderSkuMapper orderSkuMapper;


    @Override
    public Integer countOrderBySpuId(String spuId) {
        return orderSkuMapper.countOrderBySpuId(spuId);
    }
}
