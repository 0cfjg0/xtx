package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import com.itheima.xiaotuxian.vo.member.CartVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMemberCartMapper extends BaseMapper<UserMemberCart> {
    @Select("select sum(quantity) from user_member_cart where member_Id = #{userId}")
    Integer sumQuntity(@Param("userId") String userId);

    /**
     * 新增购物车信息
     * @param userMemberCart
     */
    @Insert("insert into" +
            " user_member_cart(create_time, id, member_id, sku_id, spu_id, quantity, price)" +
            " VALUES (#{createTime},#{id},#{memberId},#{skuId},#{spuId},#{quantity},#{price})")
    void saveCart( UserMemberCart userMemberCart);

    /**
     * 通过skuId,从数据库中找到spuId
     * @param id
     * @return
     */
    @Select("select spu_id from goods_sku where id = #{id}")
    String selectCart(@Param("id") String id);

    /**
     * 通过spuId,从数据库中找到spuId对应的price
     * @param spuId
     * @return
     */
    @Select("select price from goods_spu where id = #{spuId}")
    BigDecimal selectCartPriceById(@Param("spuId") String spuId);



    /**
     * 获取用户购物车列表
     * @param memberId
     */
    @Select("select * from user_member_cart where member_id = #{memberId}")
    List<UserMemberCart> getCarts(@Param("memberId") String memberId);





}
