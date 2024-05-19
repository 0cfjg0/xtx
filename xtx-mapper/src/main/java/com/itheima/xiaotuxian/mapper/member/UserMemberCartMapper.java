package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import org.apache.ibatis.annotations.*;

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


}
