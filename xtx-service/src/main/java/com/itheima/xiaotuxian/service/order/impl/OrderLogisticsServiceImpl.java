package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.order.OrderLogistics;
import com.itheima.xiaotuxian.mapper.order.OrderLogisticsMapper;
import com.itheima.xiaotuxian.service.order.OrderLogisticsService;
import org.springframework.stereotype.Service;


@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticsService {

}
