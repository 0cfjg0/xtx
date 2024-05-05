package com.itheima.xiaotuxian.service.mq.consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.mq.OperateMessage;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.mq.constant.RabbitMQConstant;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
public class GoodsOpConsumer {
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;

    /**
     * 处理 商品操作消息
     *
     * @param message 收到的消息
     */
    @RabbitListener(queues = RabbitMQConstant.GOODS_DIRECT_QUEUE)
    public void onMessage(String message) {
        log.info("receive GoodsOpMessage => {}", message);
        Stream.of(message).filter(m -> StrUtil.isNotEmpty(m) && JSONUtil.isJson(m))
                .forEach(m ->
                        Stream.of(JSONUtil.toBean(m, OperateMessage.class))
                                .filter(om -> StrUtil.isNotEmpty(om.getId()) && StrUtil.isNotEmpty(om.getOpType()))
                                .forEach(om -> {

                                    GoodsSpu spu = goodsSpuService.getById(om.getId());
                                    // 商品信息保存后，需重新审核再上架，因此需要将其从索引中删除
                                    Stream.of(om.getOpType()).filter(GoodsStatic.OP_TYPE_SAVE::equals).forEach(opType -> searchGoodsService.deleteGoods(om.getId()));
                                    // 删除商品，需要将其从索引中删除
                                    Stream.of(om.getOpType()).filter(GoodsStatic.OP_TYPE_DELETE::equals).forEach(opType -> searchGoodsService.deleteGoods(om.getId()));
                                    //商品审核通过，将商品数据添加到索引中
                                    Stream.of(om.getOpType()).filter(opType -> CollUtil.contains(Arrays.asList(GoodsStatic.OP_TYPE_FLUSH, GoodsStatic.OP_TYPE_AUDIT), opType))
                                            .forEach(opType -> searchGoodsService.saveGoods(om.getId()));
                                })
                );
    }


}
