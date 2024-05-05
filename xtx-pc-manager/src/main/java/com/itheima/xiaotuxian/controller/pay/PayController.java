package com.itheima.xiaotuxian.controller.pay;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.ijpay.alipay.AliPayApi;
import com.itheima.xiaotuxian.config.AliPayConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private PayService payService;

    @Autowired
    private AliPayConfig aliPayConfig;



}
