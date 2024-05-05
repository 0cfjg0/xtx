package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.OrderSku;

public interface OrderSkuService extends IService<OrderSku> {
    Integer countOrderBySpuId(String spuId);
}
