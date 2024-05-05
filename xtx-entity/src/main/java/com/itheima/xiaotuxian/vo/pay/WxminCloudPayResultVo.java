package com.itheima.xiaotuxian.vo.pay;

import java.util.Date;

import lombok.Data;

/*
 * @author: lbc
 * @Date: 2023-07-22 16:19:01
 * @Descripttion: 
 */
@Data
public class WxminCloudPayResultVo {
    private String appid;
    private String bankType;
    private String cashFee;
    private String feeType;
    private String isSubscribe;
    private String mchId;
    private String nonceStr;
    private String openid;
    private String outTradeNo;
    private String resultCode;
    private String returnCode;
    private String subAppid;
    private String subIsSubscribe;
    private String subMchId;
    private String subOpenid;
    // 时间格式
    private String timeEnd;
    private String totalFee;
    private String tradeType;
    private String transactionId;
    private WxminiUserInfoVo userInfo;
    
}
