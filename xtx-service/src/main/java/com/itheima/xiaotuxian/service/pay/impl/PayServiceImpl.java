package com.itheima.xiaotuxian.service.pay.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.itheima.xiaotuxian.client.CommonPayHandler;
import com.itheima.xiaotuxian.client.wechat.Config;
import com.itheima.xiaotuxian.client.wechat.Factory;
import com.itheima.xiaotuxian.client.wechat.TradingConstant;
import com.itheima.xiaotuxian.client.wechat.response.PreCreateResponse;
import com.itheima.xiaotuxian.config.wechat.WechatPayConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.RefundRecord;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.member.UserMemberOpenInfoService;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.service.order.RefundRecordService;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.util.ResponseChecker;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private UserMemberOpenInfoService userMemberOpenInfoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private OrderSkuService orderSkuService;

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    CommonPayHandler commonPayHandler;

    @Autowired
    RefundRecordService refundRecordService;

}
