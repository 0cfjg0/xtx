package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.RefundRecordVo;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.OrderPageVo;
import com.itheima.xiaotuxian.vo.order.*;

import java.util.List;

public interface OrderService extends IService<Order> {
    /**
     * 获取订单分页数据
     *
     * @param page       页码
     * @param pageSize   页尺寸
     * @param orderState 订单状态
     * @param memberId   用户id
     * @return 订单分页数据
     */


    /**
     * 统计订单数量
     *
     * @param spuId SPUid
     * @return 订单数量
     */
    Integer countOrderBySpuId(String spuId);

    /**
     * 新增订单
     *
     * @param saveVo 订单信息
     * @return 订单id
     */


//    void mockPayOrder(String orderId, String memeberId);

    List<Order> findAll(Integer orderState);

    OrderLogisticsVo consignment(Order order);


    /**
     * 申请退款
     *
     * @param order
     * @param recordVo
     * @return
     */


    List<AddressSimpleVo> getaddress(String id);

    List<OrderGoodsVo> getgoods(String id);

    OrderPreSummaryVo getsummary(String id);

    OrderResponse postOrder(OrderSaveVo orderSaveVo);

    Order getOrder(String id);

    Pager<OrderPageVo> getOrderPage(String id, Integer orderState, Integer page, Integer pageSize);

    Pager<OrderPageVo> getOrderPageAll(String id, Integer page, Integer pageSize);
}
