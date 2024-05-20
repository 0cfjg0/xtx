package com.itheima.xiaotuxian.mapper.member;
/*
 * @author: lbc
 * @Date: 2023-07-08 15:23:57
 * @Descripttion:
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;

import com.itheima.xiaotuxian.vo.member.request.LoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMemberOpenInfoMapper extends BaseMapper<UserMemberOpenInfo> {
    @Select("select * from xtx_manager.user_member where account=#{account} and password=#{password}")
    LoginVo login(LoginVo vo);

    @Select("select * from xtx_manager.user_member where  account=#{account} and password=#{password}")
    UserMember select(LoginVo vo);


}
