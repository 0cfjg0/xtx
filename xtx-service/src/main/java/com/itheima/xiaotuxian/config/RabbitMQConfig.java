package com.itheima.xiaotuxian.config;

import com.itheima.xiaotuxian.service.mq.constant.RabbitMQConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置类：配置Exchange、Queue、以及绑定交换机
 * @author itheima
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
         /**
         * 比较常用的 Converter 就是 Jackson2JsonMessageConverter,在发送消息时，它会先将自定义的消息类序列化成json格式，
         * 再转成byte构造 Message，在接收消息时，会将接收到的 Message 再反序列化成自定义的类
         */
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //开启手动ACK
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(){
        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setMandatory(true);
        /**
         * ReturnsCallback消息没有正确到达队列时触发回调，如果正确到达队列不执行
         * config : 需要开启rabbitmq发送失败回退
         * yml配置publisher-returns: true
         * 或rabbitTemplate.setMandatory(true);设置为true
         */
        //rabbitTemplate.setReturnCallback(returnedMessage -> {
        //    String messageId = returnedMessage.getMessageProperties().getMessageId();
        //    byte[] message = returnedMessage.getBody();
        //
        //    log.info("消息：{} 发送失败，消息ID：{} 应答码：{} 原因：{} 交换机：{} 路由键：{}",
        //            new String(message),messageId,replyCode,replyText,exchange,routingKey);
        //
        //});
        return rabbitTemplate;
    }

    /**
     * 声明直连交换机  支持持久化
     * @return
     */
    @Bean(RabbitMQConstant.FRONT_DIRECT_EXCHANGE)
    public Exchange fronDirectExchange(){
        return ExchangeBuilder.directExchange(RabbitMQConstant.FRONT_DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * 取消订单 消息队列
     * @return
     */
    @Bean(RabbitMQConstant.FRONT_DIRECT_QUEUE)
    public Queue frontQueue(){
        return new Queue(RabbitMQConstant.FRONT_DIRECT_QUEUE,true,false,true);
    }

    /**
     * 把取消订单消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding frontBinding(@Qualifier(RabbitMQConstant.FRONT_DIRECT_QUEUE) Queue queue,
                                      @Qualifier(RabbitMQConstant.FRONT_DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :RabbitMQConstantUtil.CANCEL_ORDER,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange)
                .with(RabbitMQConstant.FRONT_DIRECT_ROUTINGKEY).noargs();
    }


    /**
     * 声明直连交换机  支持持久化
     * @return
     */
    @Bean(RabbitMQConstant.GOODS_DIRECT_EXCHANGE)
    public Exchange goodsDirectExchange(){
        return ExchangeBuilder.directExchange(RabbitMQConstant.GOODS_DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * 取消订单 消息队列
     * @return
     */
    @Bean(RabbitMQConstant.GOODS_DIRECT_QUEUE)
    public Queue goodsQueue(){
        return new Queue(RabbitMQConstant.GOODS_DIRECT_QUEUE,true,false,true);
    }

    /**
     * 把取消订单消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding goodsBinding(@Qualifier(RabbitMQConstant.GOODS_DIRECT_QUEUE) Queue queue,
                                      @Qualifier(RabbitMQConstant.GOODS_DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :RabbitMQConstantUtil.CANCEL_ORDER,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange)
                .with(RabbitMQConstant.GOODS_DIRECT_ROUTINGKEY).noargs();
    }



    /**
     * 声明直连交换机  支持持久化
     * @return
     */
    @Bean(RabbitMQConstant.KEYWORD_DIRECT_EXCHANGE)
    public Exchange keywordDirectExchange(){
        return ExchangeBuilder.directExchange(RabbitMQConstant.KEYWORD_DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * 取消订单 消息队列
     * @return
     */
    @Bean(RabbitMQConstant.KEYWORD_DIRECT_QUEUE)
    public Queue keywordQueue(){
        return new Queue(RabbitMQConstant.KEYWORD_DIRECT_QUEUE,true,false,true);
    }

    /**
     * 把取消订单消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding keywordBinding(@Qualifier(RabbitMQConstant.KEYWORD_DIRECT_QUEUE) Queue queue,
                                @Qualifier(RabbitMQConstant.KEYWORD_DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :RabbitMQConstantUtil.CANCEL_ORDER,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange)
                .with(RabbitMQConstant.KEYWORD_DIRECT_ROUTINGKEY).noargs();
    }

}
