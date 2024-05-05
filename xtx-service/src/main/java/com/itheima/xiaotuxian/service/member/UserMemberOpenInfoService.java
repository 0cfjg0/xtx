package com.itheima.xiaotuxian.service.member;
/*
 * @author: lbc
 * @Date: 2023-07-08 16:30:24
 * @Descripttion:
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;

/**
 * 用户信息处理service
 * @author zsf
 */
public interface UserMemberOpenInfoService extends IService<UserMemberOpenInfo> {
    void updateOpenIdUser(String openid);

    /**
     * 根据用户id查询微信用户信息
     * @param userId
     * @return
     */
    UserMemberOpenInfo findWxUserByUserId(String userId);

}
