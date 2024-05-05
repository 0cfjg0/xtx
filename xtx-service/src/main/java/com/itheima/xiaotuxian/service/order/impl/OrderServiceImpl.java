package com.itheima.xiaotuxian.service.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsSkuPropertyValue;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderLogistics;
import com.itheima.xiaotuxian.entity.order.OrderLogisticsDetail;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;
import com.itheima.xiaotuxian.entity.order.RefundRecord;
import com.itheima.xiaotuxian.entity.record.RecordOrderSpu;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.goods.GoodsSkuPropertyValueMapper;
import com.itheima.xiaotuxian.mapper.order.OrderMapper;
import com.itheima.xiaotuxian.mapper.record.RecordOrderSpuMapper;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.service.member.UserMemberCartService;
import com.itheima.xiaotuxian.service.order.OrderLogisticsDetailService;
import com.itheima.xiaotuxian.service.order.OrderLogisticsService;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuPropertyService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.service.order.RefundRecordService;
import com.itheima.xiaotuxian.service.queue.DelayQueueManagerService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.BaseUtil;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.order.LogisticsDetailVo;
import com.itheima.xiaotuxian.vo.order.LogisticsVo;
import com.itheima.xiaotuxian.vo.order.OrderLogisticsVo;
import com.itheima.xiaotuxian.vo.order.OrderSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Value("${pay.expires.pc:30}")
    private Integer pcExpires;
    @Value("${pay.expires.app:3}")
    private Integer appExpires;
    @Autowired
    private UserMemberAddressService addressService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private MarketingRecommendService recommendService;
    @Autowired
    private OrderSkuService orderSkuService;
    @Autowired
    private UserMemberCartService cartService;
    @Autowired
    private RecordOrderSpuMapper recordOrderSpuMapper;
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private OrderSkuPropertyService orderSkuPropertyService;
    @Autowired
    private GoodsSkuPropertyValueMapper goodsSkuPropertyMapper;

    @Autowired
    private OrderLogisticsService orderLogisticsService;
    @Autowired
    private OrderLogisticsDetailService orderLogisticsDetailService;
    @Autowired
    private DelayQueueManagerService delayQueueManagerService;





    @Override
    public Integer countOrderBySpuId(String spuId) {
        return orderSkuService.countOrderBySpuId(spuId);
    }






    @Override
    public List<Order> findAll(Integer orderState) {
        return list(Wrappers.<Order>lambdaQuery().eq(Order::getOrderState, orderState));
    }

    /**
     * 出货
     *
     * @param order
     * @return
     */
    @Transactional
    @Override
    public OrderLogisticsVo consignment(Order order) {
        //设置发货的时间
        order.setConsignTime(LocalDateTime.now());
        order.setOrderState(OrderStatic.STATE_PENDING_RECEIPT);
        order.setConsignStatus(OrderStatic.CONSIGN_STATUS_DELIVERY);
        //模拟出三条物流信息
//        var names = new String[]{"顺峰通", "中流通", "哪都通", "原来通", "申九通", "天际通"};
//        var codes = new String[]{"14", "15", "16", "17", "18", "19"};
        var randNum = RandomUtil.randomInt(6);
        //构造物流信息
        var orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(order.getId());
        orderLogistics.setAreaNo(order.getProvinceCode());
        orderLogistics.setAddress(order.getReceiverAddress());
        orderLogistics.setLogisticsCode("14");
        orderLogistics.setLogisticsName("传智播客");
        orderLogistics.setLogisticsNo("100000000"+ RandomUtil.randomNumbers(5));
        orderLogistics.setMobile(order.getReceiverMobile());
        orderLogistics.setName(order.getReceiverContact());
        orderLogistics.setCreator("admin");
        orderLogistics.setCreateTime(LocalDateTime.now());
        orderLogistics.setUpdateTime(LocalDateTime.now());
        orderLogisticsService.save(orderLogistics);

        var orders = getOrderLogisticsDetailsByLogistics(orderLogistics);
        orders.stream().forEach(orderLogisticsDetail -> orderLogisticsDetailService.save(orderLogisticsDetail));

        order.setShippingCode(orderLogistics.getLogisticsNo());
        order.setShippingName(orderLogistics.getLogisticsName());
        this.updateById(order);


        var logisticsVo =  new LogisticsVo();
        logisticsVo.setName(orderLogistics.getName());
        logisticsVo.setNumber(orderLogistics.getLogisticsNo());
        logisticsVo.setTel(orderLogistics.getMobile());

        var logisticsDetailVos = new ArrayList<LogisticsDetailVo>();
        orders.stream().forEach(orderLogisticsDetail -> {
            var logisticsDetailVo = new LogisticsDetailVo();
            logisticsDetailVo.setId(orderLogisticsDetail.getId());
            logisticsDetailVo.setText(orderLogisticsDetail.getLogisticsInformation());
            logisticsDetailVo.setTime(orderLogisticsDetail.getLogisticsTime());
            logisticsDetailVos.add(logisticsDetailVo) ;
        });
        var orderLogisticsVo = new OrderLogisticsVo();
        orderLogisticsVo.setCount(order.getTotalNum());
        orderLogisticsVo.setPicture("");
        orderLogisticsVo.setCompany(logisticsVo);
        orderLogisticsVo.setList(logisticsDetailVos);

        return orderLogisticsVo;
    }




    @Autowired
    private RefundRecordService refundRecordService;

    /**
     * 申请退款
     * 如果订单已支付且存在退款.订单状态和支付状态不需要修改,只需要新增当前订单存在退款行为即可
     *
     * @param order
     * @param recordVo payMoney
     * @return
     */



    /**
     * 模拟物流信息
     * @param orderLogistics
     * @return
     */
    private ArrayList<OrderLogisticsDetail> getOrderLogisticsDetailsByLogistics(OrderLogistics orderLogistics) {
        var orderLogisticsDetails = new ArrayList<OrderLogisticsDetail>();
        orderLogisticsDetails.add(initOrderLogisticsDetail("小兔兔已经发货了",orderLogistics.getId(),1));
        orderLogisticsDetails.add(initOrderLogisticsDetail("小兔兔到了小猴站，小站正在分发偶",orderLogistics.getId(),2));
        orderLogisticsDetails.add(initOrderLogisticsDetail("小兔兔到了小熊站，小站正在赶往目的地",orderLogistics.getId(),3));
        orderLogisticsDetails.add(initOrderLogisticsDetail("小兔兔到了小福家里，请签收",orderLogistics.getId(),4));
        return orderLogisticsDetails;
    }
    /**
     * 模拟物流信息初始化
     * @param information
     * @param logisticsId
     * @param index
     * @return
     */
    private OrderLogisticsDetail initOrderLogisticsDetail(String information, String logisticsId, int index) {
        var orderLogisticsDetail = new OrderLogisticsDetail();
        orderLogisticsDetail.setLogisticsInformation(information);
        orderLogisticsDetail.setLogisticsTime(LocalDateTime.now().plusDays(index));
        orderLogisticsDetail.setOrderLogisticsId(logisticsId);
        orderLogisticsDetail.setCreateTime(LocalDateTime.now().plusDays(index));
        orderLogisticsDetail.setCreator("admin");
        orderLogisticsDetail.setUpdateTime(LocalDateTime.now().plusDays(index));
        return orderLogisticsDetail;
    }

}
