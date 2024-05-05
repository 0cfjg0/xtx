//package com.itheima.xiaotuxian.queue;
//
//import com.itheima.xiaotuxian.constant.statics.OrderStatic;
//import com.itheima.xiaotuxian.entity.order.DelayedOrder;
//import com.itheima.xiaotuxian.service.order.OrderService;
//import com.itheima.xiaotuxian.service.queue.DelayQueueManagerService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///*
//* @author: lbc
//* @Date: 2023-09-08 10:42:18
//* @Descripttion:
//*/
//@Slf4j
//@Component
//@Order(value = 3)
//public class LoadDelayedOrder implements ApplicationRunner {
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private DelayQueueManagerService delayQueueManagerService;
//    @Value("${pay.expires.pc:30}")
//    private Integer pcExpires;
//    @Value("${pay.expires.app:3}")
//    private Integer appExpires;
//
//    private DelayedQueueThreadFactory delayedQueueThreadFactory = new DelayedQueueThreadFactory("consumerThread");
//    private ExecutorService consumerpool = Executors.newFixedThreadPool(10, delayedQueueThreadFactory); // Executors.newCachedThreadPool(delayedQueueThreadFactory);
//
//    /**
//     * 是否开启自动取消功能
//     */
//    @Value("${order.isStarted:1}")
//    private Integer isStarted;
//
//    @Autowired
//    private DelayQueueManagerService queueManagerService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info("#######################################系统初始化#######################################");
//        loadDelayedOrder();
//        consumerDelayQueue();
//
//        log.info("#######################################初始化完成#######################################");
//    }
//    private void consumerDelayQueue() {
//        try {
//            while (isStarted == 1) {
//                DelayedOrder delayedOrder = queueManagerService.get();
//                if (null != delayedOrder) {
//                    delayedQueueThreadFactory.setNamePrefix("delayedQueue" + RandomUtils.nextInt());
//                    DelayQueueConsumer delayQueueConsumer = new DelayQueueConsumer(delayedOrder, orderService);
//                    // 提交到线程池
//                    consumerpool.execute(delayQueueConsumer);
//                    Thread.sleep(1000);
//                } else {
//                    log.info("队列为空，循环调用,消费队列的内容");
//                }
//
//            }
//        } catch (Exception e) {
//            log.error("消费异常" + e.getMessage());
//
//        }
//    }
//    public void loadDelayedOrder() {
//        log.info("【系统参数】加载中...");
//        orderService.findAll(OrderStatic.STATE_PENDING_PAYMENT).forEach(order -> {
//            LocalDateTime createTime = order.getCreateTime();
//            // 超过半个小时
//            var time = createTime.plusMinutes(pcExpires);
//            // 手机端测试，3分钟就自动改成取消状态
//            if (order.getSourceType() == 2) {
//                time = createTime.plusMinutes(appExpires);
//            }
//            // 是否超过支付要求的最后期限
//            boolean flag = LocalDateTime.now().isAfter(time);
//            if (flag) {
//                order.setPayState(OrderStatic.PAY_STATE_OVERTIME);
//                order.setOrderState(OrderStatic.STATE_CANCEL);
//                order.setCancelReason("支付超时，自动取消");
//                // update by lvbencai 2023-03-18 设置交易关闭的时间
//                order.setCloseTime(LocalDateTime.now());
//                order.setUpdateTime(LocalDateTime.now());
//                orderService.updateById(order);
//            } else {
//                delayQueueManagerService.push(order);
//            }
//        });
//        log.info("LoadDelayedOrder 执行任务 payCheck：{}", LocalDateTime.now());
//        log.info("【系统参数】加载完成...");
//    }
//
//}
