package com.itheima.xiaotuxian.controller.member;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderLogistics;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.order.OrderMapper;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSkuService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.service.member.UserMemberCartService;
import com.itheima.xiaotuxian.service.order.OrderLogisticsDetailService;
import com.itheima.xiaotuxian.service.order.OrderLogisticsService;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuPropertyService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.service.pay.PayService;
import com.itheima.xiaotuxian.vo.BatchDeleteVo;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.CartVo;
import com.itheima.xiaotuxian.vo.member.OrderBuildVo;
import com.itheima.xiaotuxian.vo.member.OrderDetailVo;
import com.itheima.xiaotuxian.vo.member.OrderPageVo;
import com.itheima.xiaotuxian.vo.member.OrderSkuPropertyVo;
import com.itheima.xiaotuxian.vo.member.OrderSkuVo;
import com.itheima.xiaotuxian.vo.order.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2022/11/2 10:24 上午
 * @Description:
 * 注释详情，add by lvbencai
 * 订单控制器，设计到支付和库存的检测
 * 具体流程：
 * 1、用户在选择商品后，向服务器端api提交包含已选择的商品
 * 2、api接手信息后，需要检查订单中商品的库存量（No.1）
 * 3、有库存，把订单数据保存到数据库中，=下单成功了，返回客户端消息，告诉客户可以进行支付，没有库存，返回提示消息
 * 4、调用服务器客户端支付接口，进行支付
 * 5、支付时候，再次进行库存检测（No.2）
 * 6、通过检测后，服务器端可以调用微信的统一下单接口进行支付，不通过，返回客户端消息
 * 7、微信返回支付结果，
 */
@Slf4j
@RestController
@RequestMapping("/member/order")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderSkuService orderSkuService;
    @Autowired
    private OrderSkuPropertyService orderSkuPropertyService;
    @Autowired
    private UserMemberAddressService addressService;
    @Autowired
    private UserMemberCartService cartService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private MarketingRecommendService recommendService;
    @Autowired
    private OrderLogisticsService orderLogisticsService;
    @Autowired
    private OrderLogisticsDetailService orderLogisticsDetailService;
    @Autowired
    private GoodsSkuService goodsSkuService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private OrderMapper ordermapper;


    /**
     * 获取我的订单
     *
     * @param page       页码
     * @param pageSize   页尺寸
     * @param orderState 订单状态
     * @return 我的订单分页数据
     */








    /**
     * 设置倒计时的值
     * @param order
     */
    private Long getCountDown(Order order) {
        AtomicReference<Long> countDown = new AtomicReference<>(-1L);
        //计算倒计时--剩余的秒数 -1 表示已经超时，正数表示倒计时未结束
        Optional.ofNullable(order.getPayLatestTime()).ifPresentOrElse(payLastTime->{
            Duration duration = LocalDateTimeUtil.between(LocalDateTime.now(),payLastTime);
            if(duration.getSeconds()>0){
                countDown.set(duration.getSeconds());
            }else{
                countDown.set(-1L);
            }
        },() -> {
            countDown.set(-1L);
        });
        return countDown.get();
    }






    /**
     * 取消订单
     *
     * @return
     */



    /**
     *  申请退款
     * @param orderId 订单id
     * @return 操作结果
     */






    /**
     * 确认收货
     *
     * @return
     */
    @PutMapping("/{id}/receipt")
    public R<OrderDetailVo> cannel(@PathVariable(name = "id") String id) {
//        var userId = getUserId();
        var userId = "1663375385531781122";
        var order = orderService.getById(id);
        if (!StrUtil.equals(userId, order.getMemberId())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_NO_PRIVILEGE);
        }
        if (!OrderStatic.STATE_PENDING_RECEIPT.equals(order.getOrderState())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_STATE_VALID);
        }
        //设置收货时间
        order.setEndTime(LocalDateTime.now());
        order.setOrderState(OrderStatic.STATE_PENDING_EVALUATION);
        orderService.updateById(order);

        var vo = BeanUtil.toBean(order, OrderDetailVo.class);
        vo.setSkus(getOrderSku(order.getId()));
        return R.ok(vo);
    }

    /**
     * 生成订单(即填写订单或结算页)
     *
     * @return 订单信息
     */
    @GetMapping("/pre")
    public R<OrderPreVo> getOrder(){
        String id = "1663375385531781122";
        List<AddressSimpleVo> address = orderService.getaddress(id);
        List<OrderGoodsVo> goods = orderService.getgoods(id);
        OrderPreSummaryVo summary = orderService.getsummary(id);
        return R.ok(new OrderPreVo(address,goods,summary));
    }







    /**
     * 删除订单
     *
     * @param
     * @return 操作结果
     */
    @PutMapping("/cancel/{id}")
    public R cancelOrder(@PathVariable String id){
        orderService.cancelOrder(id);
        return R.ok();
    }







    /**
     * 获取订单商品信息
     *
     * @param orderId 订单id
     * @return 订单商品信息
     */
    private List<OrderSkuVo> getOrderSku(String orderId) {
        return orderSkuService.list(Wrappers.<OrderSku>lambdaQuery().eq(OrderSku::getOrderId, orderId))
                .stream()
                .map(orderSku -> {
            var orderSkuVo = BeanUtil.toBean(orderSku, OrderSkuVo.class);
            var ospsvs = new ArrayList<OrderSkuPropertyVo>();

            var orderSkuProperties = new ArrayList<OrderSkuProperty>();
            orderSkuVo.setAttrsText(orderSkuPropertyService.getOrderAttrsText(orderId,orderSku.getSkuId(),getClient(),orderSkuProperties));
            orderSkuProperties.stream().forEach(orderSkuProperty -> {
                ospsvs.add(BeanUtil.toBean(orderSkuProperty, OrderSkuPropertyVo.class));
            });
            orderSkuVo.setProperties(ospsvs);
            return orderSkuVo;
        }).collect(Collectors.toList());
    }



    /**
     * 发货
     *
     * @param orderId 订单id
     * @return 操作结果
     */
    @GetMapping("/consignment/{id}")
    public R<Order> consignment(@PathVariable(name = "id") String orderId) {
        var order = orderService.getById(orderId);
        Optional.ofNullable(order).ifPresentOrElse(u->{
                    if(!OrderStatic.STATE_PENDING_DELIVERY.equals(order.getOrderState())){
                        throw new BusinessException(ErrorMessageEnum.ORDER_STATE_VALID);
                    }},
                ()->{
                    throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
                });
        orderService.consignment(order);
        return R.ok(order);
    }

    /**
     * 查看物流信息
     * @param orderId
     * @return
     */
    @GetMapping("/{id}/logistics")
    public R<OrderLogisticsVo> findLogistics(@PathVariable(name = "id") String orderId) {
         var order = orderService.getById(orderId);
        Optional.ofNullable(order).ifPresentOrElse(u->{
            if(OrderStatic.STATE_PENDING_DELIVERY.equals(order.getOrderState()) || OrderStatic.STATE_PENDING_PAYMENT.equals(order.getOrderState()  )){
                log.warn("不存在物流信息");
                return;
            }},
        ()->{
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        });
        var orderLogistics =  orderLogisticsService.getOne(Wrappers.<OrderLogistics>lambdaQuery().eq(OrderLogistics::getOrderId,orderId));
        var logisticsDetailVos = new ArrayList<LogisticsDetailVo>();

        Optional.ofNullable(orderLogistics).ifPresent(logistics->{
            var logisticsId = logistics.getId();
            Optional.ofNullable(orderLogisticsDetailService.selectList(logisticsId)).filter(CollUtil::isNotEmpty)
                    .ifPresent(list->{
                        list.stream().forEach(orderLogisticsDetail->{
                            var logisticsDetailVo = new LogisticsDetailVo();
                            logisticsDetailVo.setId(orderLogisticsDetail.getId());
                            logisticsDetailVo.setText(orderLogisticsDetail.getLogisticsInformation());
                            logisticsDetailVo.setTime(orderLogisticsDetail.getLogisticsTime());
                            logisticsDetailVos.add(logisticsDetailVo) ;
                         });
                    });
        });
        var logisticsVo =  new LogisticsVo();
        logisticsVo.setName(orderLogistics.getLogisticsName());
        logisticsVo.setNumber(orderLogistics.getLogisticsNo());
        logisticsVo.setTel(orderLogistics.getMobile());

        var orderLogisticsVo = new OrderLogisticsVo();
        orderLogisticsVo.setCount(order.getTotalNum());
        var orderSkus = orderSkuService.list(Wrappers.<OrderSku>lambdaQuery()
                                    .eq(OrderSku::getOrderId,orderId));
        orderLogisticsVo.setPicture(goodsService.getSpuPicture(orderSkus.get(0).getSpuId(), CommonStatic.REQUEST_CLIENT_PC.equals(getClient()) ? CommonStatic.MATERIAL_SHOW_PC : CommonStatic.MATERIAL_SHOW_APP));
        orderLogisticsVo.setCompany(logisticsVo);
        orderLogisticsVo.setList(logisticsDetailVos);
        return R.ok(orderLogisticsVo);
    }



    /**
     * 提交订单
     *
     * @param id 订单id
     * @return 订单信息
     */
    @GetMapping(value = "/repurchase/{id}")
    public R<OrderPreVo> repurchase(@PathVariable(name = "id") String id) {
        OrderPreVo result = new OrderPreVo();
        // 获取用户地址信息
        //暂时注释,取消令牌校验
//        String userId = getUserId();
        //暂时写死id
        String userId = "1663375385531781122";
        System.out.println(userId);
        Order originOrder = orderService.getById(id);
        //暂时注释,取消令牌校验
//        if(null == originOrder){
//            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
//        }
        List<OrderSku> orderSkus = orderSkuService.list(Wrappers.<OrderSku>lambdaQuery().eq(OrderSku::getOrderId,id));
         // 获取购物车商品信息 和 计算综述信息
        var summary = new OrderPreSummaryVo(0, BigDecimal.valueOf(0), BigDecimal.valueOf(0),
                                        BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        var aiGoodsCount = new AtomicInteger(0);
        var arTotalPrice = new AtomicReference<BigDecimal>(BigDecimal.valueOf(0));
        var arTotalPayPrice = new AtomicReference<BigDecimal>(BigDecimal.valueOf(0));
        var arDiscountPrice = new AtomicReference<BigDecimal>(BigDecimal.valueOf(0));
        result.setGoods(orderSkus.stream()
                .map(orderSku -> {
                    var vo = BeanUtil.toBean(orderSku, OrderGoodsVo.class);
                    GoodsSpu spu = goodsSpuService.getById(orderSku.getSpuId());
                    Stream.of(spu.getState()).filter(state ->
                        CollUtil.contains(Arrays.asList(GoodsStatic.STATE_SOLD_OUT, GoodsStatic.STATE_RECYCLE, GoodsStatic.STATE_HISTORY), state)).forEach(match -> {
                            throw new BusinessException(ErrorMessageEnum.ORDER_PAY_CONTAINS_VALID_GOODS);
                    });
                    GoodsSku goodsSku = goodsSkuService.getById(orderSku.getSkuId());
                     if( (null !=goodsSku.getPhysicalInventory() && goodsSku.getPhysicalInventory()<= 0) || (null != goodsSku.getSaleableInventory() && goodsSku.getSaleableInventory() <= 0)){
                         throw new BusinessException(ErrorMessageEnum.ORDER_PAY_CONTAINS_VALID_GOODS);
                     }
                    vo.setCount(orderSku.getQuantity());
                    // 原单价
                    vo.setPrice(goodsSku.getSellingPrice());
                    // 实付单价
                    vo.setPayPrice(goodsSku.getSellingPrice());
                    Optional.ofNullable(recommendService.findOne(goodsSku.getSpuId(), 1))
                            .ifPresent(recommend -> {
                                vo.setPayPrice((vo.getPrice().multiply(recommend.getDiscount())));
                                vo.setPayPrice(vo.getPrice().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP));
                    });
                    // 处理图片
                    vo.setPicture(goodsService.getSpuPicture(orderSku.getSpuId(), CommonStatic.REQUEST_CLIENT_PC.equals(getClient()) ? CommonStatic.MATERIAL_SHOW_PC : CommonStatic.MATERIAL_SHOW_APP));
                    //设置规格属性
                    vo.setAttrsText(goodsService.getGoodsAttrsText(orderSku.getSkuId(),getClient(), null));
                    // 小计总价
                    vo.setTotalPrice(vo.getPrice().multiply(new BigDecimal(orderSku.getQuantity())));
                    // 实付价格小计
                    vo.setTotalPayPrice(vo.getPayPrice().multiply(new BigDecimal(orderSku.getQuantity())));
                    // 累计商品数量
                    aiGoodsCount.set(aiGoodsCount.get() + orderSku.getQuantity());
                    // 累计商品价格总计
                    arTotalPrice.set(arTotalPrice.get().add(vo.getTotalPrice()));
                    // 累计商品应付价格总计
                    arTotalPayPrice.set(arTotalPayPrice.get().add(vo.getTotalPayPrice()));
                    // 累计商品折扣总计
                    var curDiscountPrice = vo.getTotalPrice().subtract(vo.getTotalPayPrice());
                    arDiscountPrice.set(arDiscountPrice.get().add(curDiscountPrice));
                    return vo;
        }).collect(Collectors.toList()));
        // 获取用户地址信息
        Optional.ofNullable(addressService.getAddressByUid(userId)).ifPresent(userAddresses->{
            result.setUserAddresses(userAddresses);
        });
        // 填充综述数据
        summary.setGoodsCount(aiGoodsCount.get());
        summary.setTotalPrice(arTotalPrice.get());
        summary.setDiscountPrice(arDiscountPrice.get());
        //使用原订单的邮费即可
        summary.setPostFee(originOrder.getPostFee());
        summary.setTotalPayPrice(arTotalPayPrice.get().add(summary.getPostFee()));
        result.setSummary(summary);
        return R.ok(result);
    }

    //提交订单
    @PostMapping("")
    public R<OrderResponse> postOrder(@RequestBody OrderSaveVo orderSaveVo){
        System.out.println(orderSaveVo);
        return R.ok(orderService.postOrder(orderSaveVo));
    }

    //废弃
//    //获取订单信息
//    @GetMapping("/{id}")
//    public R<OrderDetailVo> getOrder(@PathVariable String id){
//        Order order = orderService.getOrder(id);
//        Long countdown = getCountDown(order);
//        //将order对象转成封装对象
//        var orderRv = BeanUtil.toBean(order, OrderResponseVo.class);
//        orderRv.setCountdown(countdown.intValue());
//        return R.ok(orderRv);
//    }

    //获取订单信息
    @GetMapping("/{id}")
    public R<OrderDetailVo> getOrder(@PathVariable String id){
        Order order = orderService.getOrder(id);
        Long countdown = getCountDown(order);
        //将order对象转成封装对象
        var orderRv = BeanUtil.toBean(order, OrderDetailVo.class);
        orderRv.setCountdown(countdown);
        String orderid = orderRv.getId();
        List<OrderSkuVo> skus = ordermapper.getGoods(orderid);
        for (int i = 0; i < skus.size(); i++) {
            List<OrderSkuPropertyVo> properties = ordermapper.getProperties(skus.get(i).getId());
            skus.get(i).setProperties(properties);
        }
        orderRv.setSkus(skus);
        return R.ok(orderRv);
    }

    //获取个人中心订单
    @GetMapping("")
    public R<Pager<OrderPageVo>> getOrderPage(Integer orderState, Integer page, Integer pageSize){
        //暂时写死id
        String id = "1663375385531781122";
        if(orderState==0){
            Pager<OrderPageVo> paper = orderService.getOrderPageAll(id,page,pageSize);
            return R.ok(paper);
        }
        Pager<OrderPageVo> paper = orderService.getOrderPage(id,orderState,page,pageSize);
        List<OrderPageVo> list = paper.getItems();
        for (int i = 0; i < list.size(); i++) {
            //获取订单号
            String orderId = list.get(i).getId();
            //转类型
            var order = BeanUtil.toBean(list.get(i), Order.class);
            Long count = getCountDown(order);
            if(count == -1 && list.get(i).getOrderState()==1){
                ordermapper.updateOrder(orderId);
                list.get(i).setOrderState(6);
            }
            list.get(i).setCountdown(count);
        }
        paper = orderService.getOrderPage(id,orderState,page,pageSize);
        for (OrderPageVo item : paper.getItems()) {
            item.setCountdown(getCountDown(BeanUtil.toBean(item, Order.class)));
        }
        return R.ok(paper);
    }
//
//    //确认收货
//    @PutMapping("/member/order/{id}/receipt")
//    public R putOrder(@PathVariable String id){
//        orderService.putOrder(id);
//        return R.ok();
//    }
}
