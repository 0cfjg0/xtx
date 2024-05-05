package com.itheima.xiaotuxian.service.queue;


// package com.itheima.xiaotuxian.service.order;

import java.util.concurrent.DelayQueue;
/*
 * @author: lbc
 * @Date: 2023-09-07 15:51:05
 * @Descripttion: 
 */

import com.itheima.xiaotuxian.entity.order.DelayedOrder;
import com.itheima.xiaotuxian.entity.order.Order;


/**
 * 取消订单service类
 * @author zhouzhaodong
 */
public interface DelayQueueManagerService {

    DelayQueue<DelayedOrder> push(Order order);

    DelayedOrder get();

    Order remove(Order order);

}
