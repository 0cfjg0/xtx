package com.itheima.xiaotuxian.service.mq.producer;

import cn.hutool.json.JSONUtil;
import com.itheima.xiaotuxian.entity.mq.OperateMessage;
import com.itheima.xiaotuxian.service.mq.constant.RabbitMQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrontProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 发送操作信息
     *
     * @param id   操作对象id
     * @param type 操作类型，在FrontStatic中定义
     * @return 操作结果
     */
    public Boolean sendOperator(String id, String type) {
        var message = new OperateMessage();
        message.setId(id);
        message.setOpType(type);
        rabbitTemplate.convertAndSend(RabbitMQConstant.FRONT_DIRECT_EXCHANGE,
                RabbitMQConstant.FRONT_DIRECT_ROUTINGKEY, JSONUtil.toJsonStr(message));
        return true;
    }
}
