package com.itheima.xiaotuxian.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderProperties;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.OrderPageVo;
import com.itheima.xiaotuxian.vo.member.OrderSkuPropertyVo;
import com.itheima.xiaotuxian.vo.member.OrderSkuVo;
import com.itheima.xiaotuxian.vo.order.OrderGoodsVo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("select * from user_member_address where member_id = ${id}")
    List<AddressSimpleVo> getaddress(String id);

    @Select("select * from user_member_address where id = ${id}")
    List<AddressSimpleVo> getaddressById(String id);

    @Select("select u.spu_id as 'id',g.price,u.sku_id,u.quantity as 'count'\n" +
            "from user_member_cart u\n" +
            "         inner join goods_spu g on u.spu_id = g.id\n" +
            "where u.member_id = ${id};")
    List<OrderGoodsVo> getgoods(String id);

    @Select("select property_main_name,property_value_name from goods_spu_property where spu_id = ${id};")
    List<OrderProperties> getSpuProperties(String id);

    @Select("select sum(u.price)\n" +
            "from user_member_cart u\n" +
            "         inner join goods_spu g on u.spu_id = g.id\n" +
            "where u.member_id = ${id}")
    BigDecimal getSum(String id);

    @Select("select count(*)\n" +
            "from user_member_cart u\n" +
            "         inner join goods_spu g on u.spu_id = g.id\n" +
            "where u.member_id = ${id}")
    Integer getCount(String id);

    @Insert("")
    void postOrder(Order order);

    @Select("select * from order_order where id = ${id}")
    Order getOrder(String id);

    @Select("select * from goods_sku where id = ${id}")
    GoodsSku getGoodBySku(String id);

    //获取订单
    @Select("select * from order_order where member_id = ${id} and order_state = ${orderState} limit ${index},${pageSize};")
    List<OrderPageVo> selectPage(String id,Integer orderState,Integer index,Integer pageSize);

    //获取全部订单
    @Select("select * from order_order where member_id = ${id} limit ${index},${pageSize};")
    List<OrderPageVo> selectPageAll(String id,Integer index,Integer pageSize);

    //获取总数
    @Select("select count(*) from order_order where member_id = ${id} and order_state = ${orderState};")
    Integer selectPageCount(String id,Integer orderState);

    //获取全部总数
    @Select("select count(*) from order_order where member_id = ${id};")
    Integer selectPageCountAll(String id);

    //获取页数
    @Select("select count(*) from order_order where member_id = ${id} and order_state = ${orderState} limit ${index},${pageSize};")
    Integer selectPageNum(String id,Integer orderState,Integer index,Integer pageSize);

    //获取商品集合
    @Select("select * from order_order o inner join order_order_sku os on o.id = os.order_id where o.id = ${id}")
    List<OrderSkuVo> getGoods(String id);

    //获取属性集合
    @Select("select gsp.property_main_name,gsp.property_value_name from goods_sku_property_value gsp inner join order_order_sku_property oosp on gsp.sku_id = oosp.order_sku_id where oosp.order_id = ${id};")
    List<OrderSkuPropertyVo> getProperties(String id);

//    @Select("select gsp.property_main_name,gsp.property_value_name from goods_sku_property_value gsp inner join goods_sku g on gsp.sku_id = g.id where g.id = ${id};")
//    List<OrderSkuPropertyVo> getProperties(String id);

    //更新属性
    @Update("Update order_order set order_state = 6 where id = ${id};")
    void updateOrder(String id);

    //更新订单
    @Update("Update order_order set order_state = 5 where id = {id}")
    void putOrder(String id);

    //插入订单对应商品
    @Insert("insert into order_order_sku (order_id, sku_id, spu_id, image) VALUES (${orderId},${skuId},${spuId},'${image}')")
    void insertSkus(OrderSku goodsSku);

    //修改订单状态为已完成
    @Update("Update order_order set order_state = 5 where id = ${id};")
    void setOrderComplete(String id);

    //添加oosp中间表数据
    @Select("Insert into order_order_sku_property oosp (order_sku_id,order_id)")
    void InsertOosp(OrderSkuProperty orderSkuProperty);

    //取消订单
    @Update("Update order_order set order_state = 6 where id = ${id}")
    void cancelOrder(String id);

    //查询商品名字
    @Select("select name from order_order_sku where sku_id = ${id}")
    List<String> getName(String id);
}
