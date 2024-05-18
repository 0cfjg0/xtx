package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMemberCartMapper extends BaseMapper<UserMemberCart> {
    @Select("select sum(quantity) from user_member_cart where member_Id = #{userId}")
    Integer sumQuntity(@Param("userId") String userId);

    @Select("select * from user_member_cart where member_Id = #{userId}")
    List<UserMemberCart> getCartList(@Param("userId") String userId);

//    @Update("update user_member_cart set sku_id = #{skuId},selected = #{selected},count = #{count} where member_Id = #{userId}")
    UserMemberCart updateUserCart(CartSaveVo cartSaveVo, String userId);


//    void mergeCartCout(String memberId);

}
