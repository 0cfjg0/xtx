package com.itheima.xiaotuxian.client.wechat.operators;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itheima.xiaotuxian.client.wechat.Config;
import com.itheima.xiaotuxian.client.wechat.WechatPayHttpClient;
import com.itheima.xiaotuxian.client.wechat.response.H5PayResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @ClassName H5.java
 * @Description H5的支付SDK封装
 */
@Slf4j
public class H5 {

    private Config config;

    public H5(Config config) {
        this.config=config;
    }

    /***
     * @description H5下单API
     * @param outTradeNo 发起支付传递的交易单号
     * @param amount 交易金额【元】
     * @param description 商品描述
     * @param payerClientIp 客户端IP
     * @param type 场景类型 示例值：iOS, Android, Wap
     * @return
     */
    public H5PayResponse pay(String outTradeNo, String amount,
                             String description, String payerClientIp, String type) {
        //请求地址
        String uri ="/v3/pay/transactions/h5";
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
        bodyParams.putObject("scene_info").put("payer_client_ip", payerClientIp)
            .putObject("h5_info").put("type",type);
        String body = null;
        try {
            body =  httpClient.doPost(bodyParams);
        } catch (IOException e) {
            log.error("微信H5支付：pay，发生异常：{}", e.getMessage());
            throw new RuntimeException("微信H5支付：pay，发生异常!");
        }
        H5PayResponse h5PayResponse = JSONObject.parseObject(body, H5PayResponse.class);
        h5PayResponse.setCode("200");
        return h5PayResponse;
    }
}
