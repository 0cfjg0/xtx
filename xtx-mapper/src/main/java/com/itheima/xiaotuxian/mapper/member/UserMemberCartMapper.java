package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import com.itheima.xiaotuxian.vo.member.CartSelectedVo;
import com.itheima.xiaotuxian.vo.member.CartVo;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMemberCartMapper extends BaseMapper<UserMemberCart> {
    /**
     * 查用户购物车数量
     *
     * @param userId
     * @return
     */
    @Select("select sum(quantity) from user_member_cart where member_Id = #{userId}")
    Integer sumQuntity(@Param("userId") String userId);

    /**
     * 查用户购物车列表
     *
     * @param userId
     * @return
     */
    @Select("select * from user_member_cart where member_Id = #{userId}")
    List<UserMemberCart> getCartList(@Param("userId") String userId);

    @Select("select * from user_member_cart where member_Id = #{userId} and sku_id = #{skuId}")
    UserMemberCart getCart(@Param("userId") String userId, @Param("skuId") String skuId);

    /**
     * 删除购物车数据
     *
     * @param skuId
     * @param userId
     */
    @Delete("delete from user_member_cart where member_Id = #{userId} and sku_id = #{skuId}")
    void deleteUserCart(@Param("userId") String userId, @Param("skuId") String skuId);


    /**
     * 1. 新增购物车信息
     * 前端页面和数据库信息:商品数量 同步更改 3
     * (UserMemberCart 购物车商品对象  不存在情况)
     *
     * @param userMemberCart
     */
    @Insert("insert into" +
            " user_member_cart(create_time, id, member_id, sku_id, spu_id, quantity, price)" +
            " VALUES (#{createTime},#{id},#{memberId},#{skuId},#{spuId},#{quantity},#{price})")
    void saveCart(UserMemberCart userMemberCart);

    /**
     * 通过skuId,从数据库中找到spuId
     *
     * @param skuId
     * @return
     */
    @Select("select spu_id from goods_sku where id = #{skuId}")
    String selectCart(@Param("skuId") String skuId);

    /**
     * 通过spuId,从数据库中找到spuId对应的price
     *
     * @param spuId
     * @return
     */
    @Select("select price from goods_spu where id = #{spuId}")
    BigDecimal selectCartPriceById(@Param("spuId") String spuId);

    /**
     * 前端页面和数据库信息:商品数量 同步更改 1
     * 通过商品id (skuId) ,判断新增商品信息(UserMemberCart 购物车商品对象)是否存在
     *
     * @param id
     * @param skuId
     * @return
     */
    @Select("select * from user_member_cart where sku_id = #{skuId} and member_id = #{id}")
    UserMemberCart findBySkuId(@Param("id") String id, @Param("skuId") String skuId);

    /**
     * 前端页面和数据库信息:商品数量 同步更改 2
     * 通过商品id (spuId),修改购物车中已存在商品的数量(UserMemberCart 购物车商品对象  存在情况)
     *
     * @param count
     * @param id
     */
    @Update("update user_member_cart set quantity = #{count} where id = #{id}")
    void updateQuantityById(@Param("count") Integer count, @Param("id") String id);


    /**
     * 2. 获取用户购物车列表
     *
     * @param memberId
     */
    @Select("select * from user_member_cart where member_id = #{memberId}")
    List<UserMemberCart> getCarts(@Param("memberId") String memberId);


    /**
     * 3. 购物车全选/全不选
     *
     * @param cartSelectedVo
     */
    @Update("update user_member_cart set seleted = #{selected}")
    void selectAllCarts(CartSelectedVo cartSelectedVo);

    /**
     * 前端页面和数据库信息:选中状态 同步更改
     * 通过商品id (skuId) ,修改商品选中状态
     *
     * @param seleted
     * @param skuId
     */
    @Update("update user_member_cart set seleted =#{seleted} where sku_id = #{skuId} ")
    void updateSeletedBySkuId(@Param("seleted") Boolean seleted, @Param("skuId") String skuId);
}
