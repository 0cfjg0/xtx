package com.itheima.xiaotuxian.entity.order;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
 * @author: lbc
 * @Date: 2023-09-07 14:24:16
 * @Descripttion:  订单类
 */
@Slf4j
@Data
@Component
public class DelayedOrder implements Delayed {

    /**
     * 订单号
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;


     /**
     * 支付状态，1为待支付，2为已支付，3为支付超时
     */
    private Integer payState;
    /**
     * 订单状态，1为待付款、2为待发货、3为待收货、4为待评价、5为已完成、6为已取消
     */
    private Integer orderState;

     /**
     * 取消理由
     */
    private String cancelReason;
    /**
    * 交易关闭时间
    */
   private LocalDateTime closeTime;
     /**
     * 更新时间
     */
    protected LocalDateTime updateTime;

    private LocalDateTime payLatestTime;

    public DelayedOrder() {
    }

   /**
     * 获得延迟时间，用过期时间-当前时间，时间单位需要统一
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 下面用到unit.convert()方法，其实在这个小场景不需要用到，只是学习一下如何使用罢了
        return unit.convert(this.getPayLatestTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 用于延迟队列内部比较排序，当前时间的延迟时间 - 比较对象的延迟时间
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        // 这里根据取消时间来比较，如果取消时间小的，就会优先被队列提取出来
        return this.getCloseTime().compareTo(((DelayedOrder) o).getCloseTime());
    }
}
