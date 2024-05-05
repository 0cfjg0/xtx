//package com.itheima.xiaotuxian.queue;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.date.DateUtil;
//import com.itheima.xiaotuxian.constant.statics.OrderStatic;
//import com.itheima.xiaotuxian.entity.order.DelayedOrder;
//import com.itheima.xiaotuxian.entity.order.Order;
//import com.itheima.xiaotuxian.service.order.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 取消订单实现类
// *
// * @since 2023年10月18日18:47:55
// * @author lvbencai
// */
//@Slf4j
//@Component
//public class DelayQueueConsumer implements Runnable {
//
//    private DelayedOrder delayedOrder;
//    private OrderService orderService;
//
//    public DelayQueueConsumer(DelayedOrder delayedOrder, OrderService orderService) {
//        this.delayedOrder = delayedOrder;
//        this.orderService = orderService;
//    }
//
//    @Override
//    public void run() {
//        try {
//            log.info(
//                    "#######################################开始消费一次#######################################");
//            if (null != delayedOrder) {
//                Order order = BeanUtil.toBean(delayedOrder, Order.class);
//                // 重新查询下订单信息
//                var currentOrder = this.orderService.getById(order.getId());
//                if(currentOrder.getPayState().equals(OrderStatic.STATE_PENDING_PAYMENT)){
//                    // 当前订单是未支付状态
//                    orderService.updateById(order);
//                }
//            }
//            log.info("#############################订单：{},付款超时，自动取消，当前时间：{}，消费完成一次##############################",
//                    delayedOrder.getId(), DateUtil.date());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//
//    }
//
//
//}
