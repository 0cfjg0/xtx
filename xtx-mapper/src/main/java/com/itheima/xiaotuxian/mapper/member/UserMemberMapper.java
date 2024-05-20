package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMember;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface UserMemberMapper extends BaseMapper<UserMember> {
    @Insert("INSERT INTO xtx_manager.user_member (creator,id,account, mobile, password)" +
            " VALUES (#{creator},#{id},#{account}, #{mobile}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 如果表有自增主键，可以添加此注解来返回主键值
    int register(UserMember member);
}
