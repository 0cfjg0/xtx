package com.itheima.xiaotuxian.service.mq.consumer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.xiaotuxian.constant.statics.KeywordStatic;
import com.itheima.xiaotuxian.entity.mq.OperateMessage;
import com.itheima.xiaotuxian.entity.search.EsKeyword;
import com.itheima.xiaotuxian.service.goods.GoodsKeywordService;
import com.itheima.xiaotuxian.service.mq.constant.RabbitMQConstant;
import com.itheima.xiaotuxian.service.search.SearchKeywordService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class KeywordOpConsumer {
    @Autowired
    private GoodsKeywordService keywordService;
    @Autowired
    private SearchKeywordService searchKeywordService;

    /**
     * 处理 关键字操作消息
     *
     * @param message 收到的消息
     */
    @RabbitListener(queues = RabbitMQConstant.KEYWORD_DIRECT_QUEUE)
    public void onMessage(String message) {
        Stream.of(message)
                .filter(m -> StrUtil.isNotEmpty(m) && JSONUtil.isJson(m))
                .forEach(m ->
                        Stream.of(JSONUtil.toBean(m, OperateMessage.class))
                                .filter(om -> StrUtil.isNotEmpty(om.getId()) && StrUtil.isNotEmpty(om.getOpType()))
                                .forEach(om -> {
                                    //首先，按操作类型分支处理
                                    //操作类型为保存
                                    Stream.of(om.getOpType()).filter(KeywordStatic.OP_TYPE_SAVE::equals)
                                            .forEach(type ->
                                                    // 到数据库中查询关键字主体信息
                                                    Optional.ofNullable(keywordService.getById(om.getId()))
                                                            .ifPresent(goodsKeyword -> {
                                                                //当数据为启用时，写入ES
                                                                Stream.of(goodsKeyword.getState())
                                                                        .filter(state -> state == KeywordStatic.STATE_NORMAL)
                                                                        .forEach(state -> {
                                                                            var esModel = BeanUtil.toBean(goodsKeyword, EsKeyword.class);
                                                                            var associatedWords = new ArrayList<String>();
                                                                            associatedWords.addAll(Arrays.asList(esModel.getTitle().split(",")));
                                                                            //处理联想词
                                                                            Optional.ofNullable(goodsKeyword.getAssociateWords())
                                                                                    .filter(StrUtil::isNotEmpty)
                                                                                    .ifPresent(associateWords -> associatedWords.addAll(
                                                                                            Arrays.asList(associateWords.split(",")))
                                                                                    );
                                                                            esModel.setAssociatedWords(associatedWords);
                                                                            searchKeywordService.saveKeyword(esModel);
                                                                        });
                                                                //当数据为禁用时，从ES中删除
                                                                Stream.of(goodsKeyword.getState())
                                                                        .filter(state -> state == KeywordStatic.STATE_DISABLE)
                                                                        .forEach(state -> searchKeywordService.deleteKeyword(om.getId()));
                                                            })
                                            );
                                    //操作类型为删除
                                    Stream.of(om.getOpType()).filter(type -> !StrUtil.equals(KeywordStatic.OP_TYPE_SAVE, type))
                                            .forEach(type -> searchKeywordService.deleteKeyword(om.getId()));
                                })
                );
    }
}
