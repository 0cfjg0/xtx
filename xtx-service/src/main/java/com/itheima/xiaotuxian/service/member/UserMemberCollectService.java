package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;

import java.util.Map;

public interface UserMemberCollectService extends IService<UserMemberCollect> {
    Page<Map<String, Object>> findByPage(Integer page,Integer pageSize, Integer collectType, String memberId);
    Integer countCollect(String memberId,String objectId,Integer collectType);
}
