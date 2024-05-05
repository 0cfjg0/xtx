package com.itheima.xiaotuxian.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.manager.ManagerAuthority;

import java.util.List;

public interface ManagerAuthorityService extends IService<ManagerAuthority> {
    /**
     * 根据条件获取权限列表
     *
     * @param adminId 管理员ID
     * @return 权限列表
     */
    List<ManagerAuthority> findAll(String adminId);
}
