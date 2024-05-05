package com.itheima.xiaotuxian.config;

import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@Order(value = 1)
public class AliPayInitRunner implements ApplicationRunner {
    @Value("${pay.ali.appId}")
    private String aliPayAppId;
    @Value("${pay.ali.publicKey}")
    private String aliPayPublicKey;
    @Value("${pay.ali.privateKey}")
    private String aliPayPrivateKey;
    @Value("${pay.ali.serviceUrl}")
    private String aliPayServiceUrl;
    @Resource
    private AliPayConfig aliPayBean;

    @Override
    public void run(ApplicationArguments args) {
        AliPayApiConfig aliPayApiConfig = null;
        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(aliPayBean.getAppId());
            log.info("阿里支付的信息*******" + com.alibaba.fastjson.JSONObject.toJSONString(aliPayApiConfig));
        } catch (Exception e) {
//            log.warn(e.getMessage(), e);
            // aliPayApiConfig = AliPayApiConfig.builder()
            // .setAppId(aliPayBean.getAppId())
            // .setAliPayPublicKey(aliPayBean.getPublicKey())
            // .setAppCertPath(aliPayBean.getAppCertPath())
            // .setAliPayCertPath(aliPayBean.getAliPayCertPath())
            // .setAliPayRootCertPath(aliPayBean.getAliPayRootCertPath())
            // .setCharset("UTF-8")
            // .setPrivateKey(aliPayBean.getPrivateKey())
            // .setServiceUrl(aliPayBean.getServerUrl())
            // .setSignType("RSA2")
            // // 普通公钥方式
            // .build();
            // 证书模式
            // .buildByCert();
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(aliPayAppId)
                    .setAliPayPublicKey(aliPayPublicKey)
                    .setCharset("UTF-8")
                    .setPrivateKey(aliPayPrivateKey)
                    .setServiceUrl(aliPayServiceUrl)
                    .setSignType("RSA2")
                    // 普通公钥方式
                    .build();
        }
        AliPayApiConfigKit.putApiConfig(aliPayApiConfig);
//        log.info("阿里支付的信息22222222" + com.alibaba.fastjson.JSONObject.toJSONString(aliPayApiConfig));
        // AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
    }

}
