package com.itheima.xiaotuxian.controller.pay;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ijpay.alipay.AliPayApi;
import com.itheima.xiaotuxian.config.AliPayConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.order.OrderMapper;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.util.ResponseChecker;
import com.itheima.xiaotuxian.vo.R;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.UTF8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController extends BaseController {
    @Value("${pay.ali.returnUrl}")
    private String aliPayReturnUrl;
    @Value("${pay.ali.notifyUrl}")
    private String aliPayNotifyUrl;
    @Value("${pay.ali.redirectUrl}")
    private String aliPayRedirectUrl;
    @Value("${pay.ali.appId}")
    private String appId;
    @Value("${pay.ali.publicKey}")
    private String publicKey;
    @Value("${pay.ali.privateKey}")
    private String privateKey;
    @Value("${pay.ali.gatewayurl}")
    private String gatewayurl;

    @Autowired
    private PayService payService;

    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper ordermapper;

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "utf-8";
    private static final String SIGN_TYPE = "RSA2";

    @ResponseBody
    @GetMapping("/aliPay")
    public String getOrder(String orderId, String redirect) throws AlipayApiException {
        //根据订单id查询订单
        Order order = orderService.getOrder(orderId);
        //调用支付宝的支付功能
        BigDecimal money = orderService.getOrder(orderId).getPayMoney();
        System.out.println(aliPayReturnUrl);
//        aliPayReturnUrl = "http://127.0.0.1:5173/paycallback?payResult=true&orderId=";
//        aliPayReturnUrl += orderId;
        String result = sendRequestToAlipay(orderId, money.floatValue(), "xtx_cfjg");
        return result;
    }

    //支付宝官方提供的接口
    private String sendRequestToAlipay(String outTradeNo, Float totalAmount, String subject) throws AlipayApiException {
        //获得初始化的AlipayClient
        System.out.println(privateKey);
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayurl, appId, privateKey, "JSON", "utf-8", publicKey, "RSA2");
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(aliPayReturnUrl);
        System.out.println(aliPayNotifyUrl);
        alipayRequest.setNotifyUrl(aliPayNotifyUrl);

        //商品描述
        String body = "";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
    }

    @ResponseBody
    @RequestMapping("/returnUrl")
    public void returnUrlMethod(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AlipayApiException, IOException {
        System.out.println("=================================同步回调=====================================");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);
        //验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if (signVerified) {
            System.out.println("验证通过:--------------------------------------------------");
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            System.out.println("商户订单号=" + out_trade_no);
            System.out.println("支付宝交易号=" + trade_no);
            System.out.println("付款金额=" + money);

            //数据库操作
//            orderService.setOrderComplete(out_trade_no);
            //跳转到结果页面T/F
            String redirect = "http://127.0.0.1:5173/paycallback?payResult=true&orderId=" + out_trade_no;
            response.sendRedirect(redirect);
        } else {
            String redirect = "http://127.0.0.1:5173/paycallback?payResult=false&orderId=";
            response.sendRedirect(redirect);
        }
    }

    @ResponseBody
    @PostMapping("/notifyUrl")  // 注意这里必须是POST接口
//    @RequestMapping("/notifyUrl")
    public String NotifyUrlUrlMethod(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AlipayApiException, UnsupportedEncodingException, UnsupportedEncodingException {
        System.out.println("=================================异步回调=====================================");
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        //验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if (signVerified) {
            System.out.println("================================验签通过====================================");
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            System.out.println("商户订单号=" + out_trade_no);
            System.out.println("支付宝交易号=" + trade_no);
            System.out.println("付款金额=" + money);
            orderService.setOrderComplete(out_trade_no);
            System.out.println("=================================修改成功=====================================");
            System.out.println("=================================异步回调结束=====================================");
            return "success";
        } else {
            return "fail";
        }
    }

    @SneakyThrows(value = AlipayApiException.class)
    @ResponseBody
    @GetMapping("/refund")
    public Boolean refund(String outTradeNo, String refundAmount, String refundReason) {
        System.out.println("=================================发起订单退款,订单号:" + outTradeNo + "=====================================");
        AlipayClient alipayClient = initAlipay();
        AlipayTradeRefundRequest alipayTradeCloseRequest = new AlipayTradeRefundRequest();
        //请求参数集合对象,除了公共参数之外,所有参数都可通过此对象传递
        AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
        //退款的订单号,传入生成支付订单时的订单号即可
        alipayTradeRefundModel.setOutTradeNo(outTradeNo);
        //退款金额
        alipayTradeRefundModel.setRefundAmount(refundAmount);
        //退款的原因
        alipayTradeRefundModel.setRefundReason(refundReason);
        alipayTradeCloseRequest.setBizModel(alipayTradeRefundModel);
        //退款的执行流程与支付不太一样,支付时成功之后，需要通知回调接口,而退款则不需要,只需判断响应			参数 refundResponse.getFundChange().equals("Y") 判断是否发生了资金变化, equals("Y")表示资金发生了变化，退款成功
        AlipayTradeRefundResponse response = alipayClient.execute(alipayTradeCloseRequest);
        Boolean result = response.isSuccess() && response.getFundChange().equals("Y");
        //如果删除成功删除订单
        if(result==true){
            ordermapper.deleteOrder(outTradeNo);
        }
        String text = result == true ? "=================================订单退款成功=====================================" : "=================================订单退款失败=====================================";
        System.out.println(text);
        System.out.println("=================================订单退款结束:" + outTradeNo + "=====================================");
        return result;
    }

    @SneakyThrows(value = AlipayApiException.class)
    public AlipayClient initAlipay() {
        //构造client
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId(appId);
        //设置应用私钥
        certAlipayRequest.setPrivateKey(privateKey);
        //设置请求格式，固定值json
        certAlipayRequest.setFormat(FORMAT);
        //设置字符集
        certAlipayRequest.setCharset(CHARSET);
        //设置签名类型
        certAlipayRequest.setSignType(SIGN_TYPE);
        //不用证书
//        //设置应用公钥证书路径
//        certAlipayRequest.setCertPath(CERT_PATH);
//        //设置支付宝公钥证书路径
//        certAlipayRequest.setAlipayPublicCertPath(AliPayUtil.ALIPAY_PUBLIC_CER_PATH);
//        //设置支付宝根证书路径
//        certAlipayRequest.setRootCertPath(AliPayUtil.ROOT_CER_PATH);
        //构造client
        return new DefaultAlipayClient(certAlipayRequest);
    }

}
