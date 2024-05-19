package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMemberCartMapper extends BaseMapper<UserMemberCart> {
    @Select("select sum(quantity) from user_member_cart where member_Id = #{userId}")
    Integer sumQuntity(@Param("userId") String userId);
}