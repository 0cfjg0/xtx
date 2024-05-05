package com.itheima.xiaotuxian.service.mq.constant;

/**
 * RabbitMQ RoutingKey 常量工具类
 * @author itheima
 */
public class RabbitMQConstant {

    /**
     * 交换机名称
     */
    public static final String FRONT_DIRECT_EXCHANGE = "fde_directExchange";
    public static final String GOODS_DIRECT_EXCHANGE = "gde_directExchange";
    public static final String KEYWORD_DIRECT_EXCHANGE = "kde_directExchange";


    public static final String FRONT_DIRECT_QUEUE = "fde_directExchange_queue";
    public static final String FRONT_DIRECT_ROUTINGKEY = "fde_direct_routingkey";
    public static final String GOODS_DIRECT_QUEUE = "gde_directExchange_queue";
    public static final String GOODS_DIRECT_ROUTINGKEY = "gde_direct_routingkey";
    public static final String KEYWORD_DIRECT_QUEUE = "kde_directExchange_queue";
    public static final String KEYWORD_DIRECT_ROUTINGKEY = "kde_direct_routingkey";


}
