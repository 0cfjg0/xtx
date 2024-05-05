package com.itheima.xiaotuxian.service.member.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.member.UserMemberProperty;
import com.itheima.xiaotuxian.mapper.member.UserMemberPropertyMapper;
import com.itheima.xiaotuxian.service.member.UserMemberPropertyService;
import org.springframework.stereotype.Service;

@Service
public class UserMemberPropertyServiceImpl extends ServiceImpl<UserMemberPropertyMapper, UserMemberProperty> implements UserMemberPropertyService {
}
