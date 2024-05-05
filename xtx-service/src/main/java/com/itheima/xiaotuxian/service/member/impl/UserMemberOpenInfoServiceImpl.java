package com.itheima.xiaotuxian.service.member.impl;
/*
 * @author: lbc
 * @Date: 2023-07-08 16:31:57
 * @Descripttion: 
 */

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;
import com.itheima.xiaotuxian.mapper.member.UserMemberOpenInfoMapper;
import com.itheima.xiaotuxian.service.member.UserMemberOpenInfoService;
import org.springframework.stereotype.Service;

/**
 * @author zsf
 * @since 2023年5月9日16:49:13
 */
@Service
public class UserMemberOpenInfoServiceImpl extends ServiceImpl<UserMemberOpenInfoMapper, UserMemberOpenInfo>
        implements UserMemberOpenInfoService {

    /**
     * @description:
     * @param {String} openid
     * @return {*}
     * @author: lbc
     */
    @Override
    public void updateOpenIdUser(String openid) {
        UserMemberOpenInfo userMemberOpenInfo = new UserMemberOpenInfo();
        userMemberOpenInfo.setIsDelete(1);
        this.getBaseMapper().update(userMemberOpenInfo, Wrappers.<UserMemberOpenInfo>lambdaUpdate()
                .eq(UserMemberOpenInfo::getOpenId, openid).eq(UserMemberOpenInfo::getIsDelete, 0));

    }



    @Override
    public UserMemberOpenInfo findWxUserByUserId(String userId) {
        UserMemberOpenInfo userOpneInfo = this.baseMapper.selectOne(Wrappers.<UserMemberOpenInfo>lambdaQuery()
                .eq(UserMemberOpenInfo::getUserId,userId)
                .eq(UserMemberOpenInfo::getIsDelete,0));
        if(null != userOpneInfo){
            return userOpneInfo;
        }
        return null;
    }
}
