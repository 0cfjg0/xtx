package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.OrderLogisticsDetail;

import java.util.List;

public interface OrderLogisticsDetailService extends IService<OrderLogisticsDetail> {
    List<OrderLogisticsDetail> selectList(String logisticsId);
}
