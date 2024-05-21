package com.itheima.xiaotuxian;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author: itheima
 * @Date: 2023/7/20 10:41 上午
 * @Description:
 */
@EnableCaching
@SpringBootApplication
public class XiaotuxianPCApplication {
    public static void main(String[] args) {
//        test();
        SpringApplication.run(XiaotuxianPCApplication.class, args);
    }

//    public static void test(){
//        String body =  "{\"alipay_trade_query_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"tls***@sandbox.com\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088622955532108\",\"buyer_user_type\":\"PRIVATE\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"1607695672389095425\",\"passback_params\":\"http:\\/\\/erabbit.itheima.net\\/#\\/pay\\/callback\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2022-12-27 19:15:21\",\"total_amount\":\"256.00\",\"trade_no\":\"2022122722001432100502421903\",\"trade_status\":\"TRADE_SUCCESS\"},\"sign\":\"RXKmo3IlaiJj7gFxPI3G/Ls/Ban7+O6WSLWouC1JV8FNkLWtcW2gydey5YtTWdphSBxqimlKdUbjf+SLAtNtTziIJAawqsvZftQSsMgvgeRkSxVTI/1EpGDNm0F1KSiA22XtI/OQ34wdD1YOceeS7M+/1FWMDp/XApTMltDvigWLdaSmZ+amS8wo3FzMVIiKLL1UprLWFXMIhGdtgl1pZE8P0+5Npr8djUnOVvKA2d5n5ZnRzjK8DagzSM9/RiSq6ROUJgNRU4d10kI3Tg9Y7SxSqc9TSvUEB7vNA+CDPRHJIkTgkgFj7mBF9uDFdJYT+t5lEXQYdaOEzzquxwey5w==\"}";
//        var bodyJson2 = cn.hutool.json.JSONUtil.parseObj(body);
//        String aliPayRedirectUrl2 = bodyJson2.getByPath("alipay_trade_query_response.passback_params").toString();
//        System.out.println(aliPayRedirectUrl2);
//    }
}
