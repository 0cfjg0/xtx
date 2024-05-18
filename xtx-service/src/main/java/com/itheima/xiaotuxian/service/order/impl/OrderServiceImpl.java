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
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
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
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsDetailVo;
import com.itheima.xiaotuxian.vo.member.*;
import com.itheima.xiaotuxian.vo.order.*;
import io.swagger.models.auth.In;
import org.aspectj.weaver.ast.Or;
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
    @Autowired
    private OrderMapper ordermapper;




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

    //获取地址
    @Override
    public List<AddressSimpleVo> getaddress(String id) {
        return ordermapper.getaddress(id);
    }

    //获取商品信息
    @Override
    public List<OrderGoodsVo> getgoods(String id) {
        List<OrderGoodsVo> list = ordermapper.getgoods(id);
        for (int i = 0; i < list.size(); i++) {
            String goodid = list.get(i).getId();
            OrderGoodsVo orderGoodsVo = list.get(i);
            GoodsDetailVo goodsDetailVo = goodsService.findGoodsById(goodid);
            orderGoodsVo.setId(goodsDetailVo.getId());
            orderGoodsVo.setName(goodsDetailVo.getName());
            orderGoodsVo.setPicture(goodsDetailVo.getMainPictures().getPc().get(0).getUrl());
            BigDecimal count = ordermapper.getSum(id);
            orderGoodsVo.setCount(ordermapper.getCount(id));
            //小计
            orderGoodsVo.setTotalPrice(count);
            //实付
            orderGoodsVo.setTotalPayPrice(count);
            System.out.println("url---------------:" + goodsDetailVo.getMainPictures().getPc().get(0).getUrl());
        }
        return list;
    }

    //获取订单结算页的summary
    @Override
    public OrderPreSummaryVo getsummary(String id) {
        OrderPreSummaryVo orderPreSummaryVo = new OrderPreSummaryVo();
        orderPreSummaryVo.setTotalPrice(ordermapper.getSum(id));
        //写死邮费
        orderPreSummaryVo.setPostFee(BigDecimal.valueOf(6));
        orderPreSummaryVo.setGoodsCount(ordermapper.getCount(id));
        orderPreSummaryVo.setTotalPayPrice(orderPreSummaryVo.getTotalPrice().add(orderPreSummaryVo.getPostFee()));
        return orderPreSummaryVo;
    }

    @Override
    public OrderResponse postOrder(OrderSaveVo orderSaveVo) {
        Order order = new Order();
        order.setCreator("1663375385531781122");
        order.setMemberId("1663375385531781122");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setPayType(orderSaveVo.getPayType());
        order.setPayChannel(orderSaveVo.getPayChannel());
        order.setPayLatestTime(LocalDateTime.now().minusMinutes(-15));
        List<GoodsSku> listsku = new ArrayList<>();
        BigDecimal sum = BigDecimal.valueOf(0);
        for (CartVo good : orderSaveVo.getGoods()) {
            listsku.add(ordermapper.getGoodBySku(good.getSkuId()));
        }
        for (GoodsSku goodsSku : listsku) {
            sum = sum.add(goodsSku.getSellingPrice());
        }
        order.setPayMoney(sum);
        //获取地址
        List<AddressSimpleVo> list = ordermapper.getaddressById(orderSaveVo.getAddressId());
        AddressSimpleVo address = new AddressSimpleVo();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getIsDefault()==0){
                address = list.get(i);
                break;
            }
        }
        order.setReceiverContact(address.getReceiver());
        order.setReceiverAddress(address.getAddress());
        order.setReceiverMobile(address.getContact());
        System.out.println(order);
        super.save(order);
        return new OrderResponse(order.getId(),"1","1");
    }

    @Override
    public Order getOrder(String id) {
        Order order = ordermapper.getOrder(id);
        return order;
    }

    @Override
    public Pager<OrderPageVo> getOrderPage(String id, Integer orderState, Integer page, Integer pageSize) {
        //分页查询
        Integer index = (page-1)*pageSize;
        System.out.println(index);
        List<OrderPageVo> list = ordermapper.selectPage(id,orderState,index,pageSize);
        for (int i = 0; i < list.size(); i++) {
            //获取订单号
            String orderId = list.get(i).getId();
            //获取商品集合
            List<OrderSkuVo> skus = ordermapper.getGoods(orderId);
            for (int i1 = 0; i1 < skus.size(); i1++) {
                //获取属性集合
                skus.get(i1).setProperties(ordermapper.getProperties(orderId));
            }
            list.get(i).setSkus(skus);
        }
        Pager<OrderPageVo> pager = new Pager<>();
        pager.setPage(page);
        pager.setPageSize(pageSize);
        //获取总记录数
        Integer count = ordermapper.selectPageCount(id,orderState);
        pager.setCounts(count);
        //获取总页数
//        Integer pages = ordermapper.selectPageNum(id,orderState,index,pageSize);
        Integer pages = count/pageSize + 1;
        pager.setPages(pages);
        pager.setItems(list);
        return pager;
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
