package com.itheima.xiaotuxian.service.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.entity.mq.OperateMessage;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.service.mq.constant.RabbitMQConstant;
import com.itheima.xiaotuxian.service.mq.producer.GoodsProducer;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class FrontOpConsumer {
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private GoodsProducer producer;
    private static final int SIZE = 5000;


    /**
     * 处理 前台分类操作消息
     *
     * @param message 收到的消息
     */
    @RabbitListener(queues = RabbitMQConstant.FRONT_DIRECT_QUEUE)
    public void onMessage(String message) {
        log.info("receive frontOpMessage => {}", message);
        Stream.of(message).filter(m -> StrUtil.isNotEmpty(m) && JSONUtil.isJson(m))
                .forEach(m ->
                        Stream.of(JSONUtil.toBean(m, OperateMessage.class))
                                .filter(om -> StrUtil.isNotEmpty(om.getId()) && StrUtil.isNotEmpty(om.getOpType()))
                                .forEach(om ->
                                        // 操作前台分类关联关系
                                        Stream.of(om.getOpType()).filter(FrontStatic.OP_TYPE_RELATION::equals).forEach(opType ->
                                                Stream.of(searchGoodsService.countByFrontRelation(om.getId())).filter(total -> total > 0)
                                                        .forEach(total -> {
                                                            Stream.of(total).filter(t -> t <= SIZE).forEach(t -> processGoods(om.getId(), 1));
                                                            Stream.of(total).filter(t -> t > SIZE).forEach(t -> processGoods(om.getId(), (t.intValue() / SIZE) + 1));
                                                        })
                                        ))
                );
    }

    /**
     * 处理商品信息
     *
     * @param relationKey 前台数据关联信息
     * @param totalPage   总页数
     */
    private void processGoods(String relationKey, int totalPage) {
        var pageIndex = 1;
        var recordCount = SIZE;
        var dataIds = new ArrayList<String>();
        while (recordCount == SIZE && pageIndex <= totalPage) {
            var datas = searchGoodsService.findAllByFrontRelation(relationKey, pageIndex, SIZE);
            dataIds.addAll(datas.stream().map(EsGoods::getId).collect(Collectors.toList()));
            pageIndex = pageIndex + 1;
            recordCount = datas.size();
        }
        dataIds.stream().distinct().forEach(id -> producer.sendOperator(id, GoodsStatic.OP_TYPE_FLUSH));
    }
}
