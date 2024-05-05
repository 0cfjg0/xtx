package com.itheima.xiaotuxian.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.manager.ManagerAdmin;

public interface ManagerAdminService extends IService<ManagerAdmin> {
    /**
     * 登录
     *
     * @param managerAdmin 账号密码
     * @return 用户信息
     */
    ManagerAdmin login(ManagerAdmin managerAdmin);

    /**
     * 保存管理员信息
     *
     * @param managerAdmin 管理员信息
     * @return 操作结果
     */
    Boolean saveAndUpdate(ManagerAdmin managerAdmin);

    boolean hasRole(String role, ManagerAdmin user);
}
