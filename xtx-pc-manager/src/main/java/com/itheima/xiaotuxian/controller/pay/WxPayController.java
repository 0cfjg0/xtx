package com.itheima.xiaotuxian.controller.pay;

import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wxpay")
public class WxPayController extends BaseController {

    @Autowired
    private PayService payService;


}
