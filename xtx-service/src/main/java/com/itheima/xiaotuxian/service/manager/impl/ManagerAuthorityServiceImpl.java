package com.itheima.xiaotuxian.service.manager.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.manager.ManagerAuthority;
import com.itheima.xiaotuxian.mapper.manager.ManagerAuthorityMapper;
import com.itheima.xiaotuxian.service.manager.ManagerAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:31 下午
 * @Description:
 */
@Service
public class ManagerAuthorityServiceImpl extends ServiceImpl<ManagerAuthorityMapper, ManagerAuthority> implements ManagerAuthorityService {
    @Autowired
    private ManagerAuthorityMapper authorityMapper;


    @Override
    public List<ManagerAuthority> findAll(String adminId) {
        return authorityMapper.findAll(adminId);
    }
}
