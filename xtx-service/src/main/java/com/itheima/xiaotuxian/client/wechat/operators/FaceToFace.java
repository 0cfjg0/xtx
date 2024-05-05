package com.itheima.xiaotuxian.client.wechat.operators;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itheima.xiaotuxian.client.wechat.Config;
import com.itheima.xiaotuxian.client.wechat.WechatPayHttpClient;
import com.itheima.xiaotuxian.client.wechat.response.PreCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @ClassName FaceToFace.java
 * @Description 面對面支付
 */
@Slf4j
public class FaceToFace {

    private Config config;

    public FaceToFace(Config config) {
        this.config=config;
    }

    /**
     * @description 扫用户出示的付款码，完成付款
     * @param outTradeNo 交易单号
     * @param amount 交易金额
     * @param description 描述
     * @return  支付结果
     */
    public PreCreateResponse precreate(String outTradeNo, String amount, String description) throws Exception {
        //请求地址
        String uri ="/v3/pay/transactions/native";
        //系统参数封装
        WechatPayHttpClient httpClient = WechatPayHttpClient.builder()
            .mchId(config.getMchId())
            .mchSerialNo(config.getMchSerialNo())
            .apiV3Key(config.getApiV3Key())
            .privateKey(config.getPrivateKey())
            .domain(config.getDomain()+uri)
            .build();
        //业务数据的封装
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode bodyParams = objectMapper.createObjectNode();
        BigDecimal bigDecimal = new BigDecimal(amount);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        bodyParams.put("mchid",config.getMchId())
                .put("appid",config.getAppid() )
                .put("description", description)
                .put("notify_url", config.getNotifyUrl())
                .put("out_trade_no", outTradeNo);
        bodyParams.putObject("amount")
                .put("total", multiply.intValue());
        String body =  httpClient.doPost(bodyParams);
        PreCreateResponse preCreateResponse = JSONObject.parseObject(body, PreCreateResponse.class);
        if (StringUtils.isNotBlank(preCreateResponse.getCodeUrl())){
            preCreateResponse.setCode("200");
            preCreateResponse.setMessage("请求成功");
            preCreateResponse.setSubCode("10000");
            preCreateResponse.setSubMessage("FaceToFace支付CodeUrl生成成功");
        }
        return preCreateResponse;

    }

}
