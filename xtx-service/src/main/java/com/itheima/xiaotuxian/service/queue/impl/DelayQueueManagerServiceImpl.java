package com.itheima.xiaotuxian.service.queue.impl;


import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;

import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.entity.order.DelayedOrder;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.service.queue.DelayQueueManagerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 取消订单实现类
 * @since 2023年10月18日18:47:55
 * @author lvbencai
 */
@Slf4j
@Service
public class DelayQueueManagerServiceImpl implements DelayQueueManagerService {

    /**
     * 是否开启自动取消功能
     */
    @Value("${order.isStarted:1}")
    private int isStarted;

    /**
     * 延迟队列，用来存放订单对象
     */
    DelayQueue<DelayedOrder> queue = new DelayQueue<>();

    @Override
    public DelayQueue<DelayedOrder> push(Order order){
        try {
            DelayedOrder delayedOrder =  new DelayedOrder();
            delayedOrder.setCancelReason("支付超时,自动取消订单");
            delayedOrder.setCloseTime(order.getPayLatestTime());
            delayedOrder.setPayLatestTime(order.getPayLatestTime());
            delayedOrder.setUpdateTime(LocalDateTime.now());
            delayedOrder.setId(order.getId());
            delayedOrder.setOrderState(OrderStatic.STATE_CANCEL);
            delayedOrder.setPayState(OrderStatic.PAY_STATE_OVERTIME);
            delayedOrder.setUserId(order.getCreator());
            queue.add(delayedOrder);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        return queue;
    }





    @Override
    public DelayedOrder get() {
         try {
            DelayedOrder delayedOrder =  queue.take();
            if(null != delayedOrder){
                return delayedOrder;
            }
        } catch (InterruptedException e) {
//             e.printStackTrace();
             log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public Order remove(Order order) {
        var flag = queue.remove(order);
        if(flag){
            return  order;
        }
        return null;
    }

}
