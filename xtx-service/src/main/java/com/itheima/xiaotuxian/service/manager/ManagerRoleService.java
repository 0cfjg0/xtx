package com.itheima.xiaotuxian.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.manager.ManagerRole;

import java.util.List;

public interface ManagerRoleService extends IService<ManagerRole> {
    List<ManagerRole> findAll( String adminId);

}
