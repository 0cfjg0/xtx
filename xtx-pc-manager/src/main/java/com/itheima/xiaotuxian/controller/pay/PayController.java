package com.itheima.xiaotuxian.controller.pay;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ijpay.alipay.AliPayApi;
import com.itheima.xiaotuxian.config.AliPayConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.UTF8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    private static final String GATEWAY_URL ="https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT ="JSON";
    private static final String CHARSET ="utf-8";
    private static final String SIGN_TYPE ="RSA2";

    @ResponseBody
    @GetMapping("/aliPay")
    public String getOrder(String orderId, String redirect) throws AlipayApiException {
        //根据订单id查询订单
        Order order = orderService.getOrder(orderId);
        //调用支付宝的支付功能
        BigDecimal money = orderService.getOrder(orderId).getPayMoney();
        System.out.println(aliPayReturnUrl);
        aliPayReturnUrl = "http://127.0.0.1:5173/paycallback?payResult=true&orderId=";
        aliPayReturnUrl += orderId;
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

        //商品描述（可空）
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

    @GetMapping("")

    @PostMapping("/notifyUrl")
    public void returnUrlMethod(HttpServletRequest request, HttpSession session, Model model) throws AlipayApiException, UnsupportedEncodingException, UnsupportedEncodingException {
        System.out.println("=================================异步回调=====================================");
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
        }
        //验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            System.out.println("商户订单号="+out_trade_no);
            System.out.println("支付宝交易号="+trade_no);
            System.out.println("付款金额="+money);
        }
    }

}
