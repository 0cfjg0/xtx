package com.itheima.xiaotuxian.service.manager.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.manager.ManagerRole;
import com.itheima.xiaotuxian.mapper.manager.ManagerRoleMapper;
import com.itheima.xiaotuxian.service.manager.ManagerRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:31 下午
 * @Description:
 */
//@Service
@Service("ss")
public class ManagerRoleServiceImpl extends ServiceImpl<ManagerRoleMapper, ManagerRole> implements ManagerRoleService {
    @Autowired
    private ManagerRoleMapper roleMapper;
    @Autowired
    private ManagerRoleService roleService;


    @Override
    public List<ManagerRole> findAll(String adminId) {
        return roleMapper.findAll(adminId);
    }


}
