package com.itheima.xiaotuxian.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.manager.ManagerRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ManagerRoleMapper extends BaseMapper<ManagerRole> {
    List<ManagerRole> findAll(@Param(value = "adminId") String adminId);

}
