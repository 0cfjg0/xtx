package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderSku;

import java.util.List;

public interface OrderSkuService extends IService<OrderSku> {
    Integer countOrderBySpuId(String spuId);

    void InsertSku(List<GoodsSku> listsku, Order order);
}
