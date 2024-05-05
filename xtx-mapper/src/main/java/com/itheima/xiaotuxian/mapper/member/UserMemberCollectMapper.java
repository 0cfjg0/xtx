package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserMemberCollectMapper extends BaseMapper<UserMemberCollect> {
    Page<Map<String,Object>> findByPage(Page<UserMemberCollect> page, @Param("collectType")Integer collectType, @Param("memberId")String memberId);
}
