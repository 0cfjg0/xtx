package com.itheima.xiaotuxian.config.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName WechatPayProperties.java
 * @Description TODO
 */
@Data
@ConfigurationProperties(prefix = "pay.wx")
public class WechatPayProperties {

    //appId
    String appid;

    //商户号
    String mchId;

    //私钥字符串
    String privateKey;

    //商户证书序列号
    String mchSerialNo;

    //V3密钥
    String apiV3Key;

    //请求地址
    String domain;

    //回调地址
    String notifyUrl;

    //退款回调地址
    String refundNotifyUrl;
}
