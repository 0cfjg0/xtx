package com.itheima.xiaotuxian.service.member.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.member.UserMemberFavorite;
import com.itheima.xiaotuxian.mapper.member.UserMemberFavoriteMapper;
import com.itheima.xiaotuxian.service.member.UserMemberFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class UserMemberFavoriteServiceImpl extends ServiceImpl<UserMemberFavoriteMapper, UserMemberFavorite> implements UserMemberFavoriteService {
}
