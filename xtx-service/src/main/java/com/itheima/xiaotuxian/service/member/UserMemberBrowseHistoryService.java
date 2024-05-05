package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberBrowseHistory;

public interface UserMemberBrowseHistoryService extends IService<UserMemberBrowseHistory> {
    void saveHistory(String memberId,String spuId);
}
