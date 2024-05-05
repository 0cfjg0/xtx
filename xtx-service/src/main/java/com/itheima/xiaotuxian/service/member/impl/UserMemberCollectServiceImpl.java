package com.itheima.xiaotuxian.service.member.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import com.itheima.xiaotuxian.mapper.member.UserMemberCollectMapper;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserMemberCollectServiceImpl extends ServiceImpl<UserMemberCollectMapper, UserMemberCollect> implements UserMemberCollectService {

    @Override
    public Page<Map<String, Object>> findByPage(Integer page, Integer pageSize, Integer collectType, String memberId) {
        var ipage = new Page<UserMemberCollect>(page, pageSize);
        return baseMapper.findByPage(ipage, collectType, memberId);
    }

    @Override
    public Integer countCollect(String memberId, String objectId, Integer collectType) {
        return this.count(Wrappers.<UserMemberCollect>lambdaQuery()
                .eq(StrUtil.isNotEmpty(objectId), UserMemberCollect::getCollectObjectId, objectId)
                .eq(collectType != null, UserMemberCollect::getCollectType, collectType)
                .eq(StrUtil.isNotEmpty(memberId), UserMemberCollect::getMemberId, memberId));
    }
}
