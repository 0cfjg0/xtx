package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
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

//    //华丽的分割线
//    @Insert("insert into" +
//            " user_member_cart(create_time, id, member_id, sku_id, spu_id, quantity, price)" +
//            " VALUES (#{createTime},#{id},#{memberId},#{skuId},#{spuId},#{quantity},#{price})")
//    void saveCart(UserMemberCart userMemberCart);
//
//    /**
//     * 通过skuId,从数据库中找到spuId
//     *
//     * @param id
//     * @return
//     */
//    @Select("select spu_id from goods_sku where id = #{id}")
//    String selectCart(@Param("id") String id);
//
//    /**
//     * 通过spuId,从数据库中找到spuId对应的price
//     *
//     * @param spuId
//     * @return
//     */
//    @Select("select price from goods_spu where id = #{spuId}")
//    BigDecimal selectCartPriceById(@Param("spuId") String spuId);

}
