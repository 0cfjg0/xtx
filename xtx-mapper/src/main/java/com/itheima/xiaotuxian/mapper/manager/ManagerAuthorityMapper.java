package com.itheima.xiaotuxian.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.manager.ManagerAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ManagerAuthorityMapper extends BaseMapper<ManagerAuthority> {
    List<ManagerAuthority> findAll(@Param(value = "adminId") String adminId);
}
