package com.itheima.xiaotuxian.config;
/*
 * @author: lbc
 * @Date: 2023-06-11 14:24:23
 * @Descripttion:
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/*
 * @author: lbc
 * @Date: 2023-06-11 14:24:23
 * @Descripttion:
 */
@Configuration
@ConfigurationProperties(prefix = "pay.wx-mini")
@Slf4j
@Data
public class WxMiniPayConfig{
    private String appId;
    private String appSecret;
    private String mchId;
    private String partnerKey;
    private String certPath;
    private String domain;
    private String notifyUrl;
    private String refundNotifyUrl;
    private String seesionHost;
    private String grantType;
}
