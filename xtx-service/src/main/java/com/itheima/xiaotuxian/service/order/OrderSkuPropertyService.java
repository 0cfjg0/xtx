package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;

import java.util.ArrayList;

public interface OrderSkuPropertyService extends IService<OrderSkuProperty> {
    String getOrderAttrsText(String orderId, String skuId, String client, ArrayList<OrderSkuProperty> ospsvs);
}
