package com.itheima.xiaotuxian.service.member.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.member.UserMemberBrowseHistory;
import com.itheima.xiaotuxian.mapper.member.UserMemberBrowseHistoryMapper;
import com.itheima.xiaotuxian.service.member.UserMemberBrowseHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserMemberBrowseHistoryServiceImpl extends ServiceImpl<UserMemberBrowseHistoryMapper, UserMemberBrowseHistory> implements UserMemberBrowseHistoryService {

    @Override
    public void saveHistory(String memberId, String spuId) {
        Optional.ofNullable(this.getOne(Wrappers.<UserMemberBrowseHistory>lambdaQuery()
                .eq(UserMemberBrowseHistory::getMemberId, memberId)
                .eq(UserMemberBrowseHistory::getSpuId, spuId))).ifPresentOrElse(history -> {
            history.setUpdateTime(LocalDateTime.now());
            this.updateById(history);
        }, () -> {
            var history = new UserMemberBrowseHistory();
            history.setMemberId(memberId);
            history.setSpuId(spuId);
            history.setCreateTime(LocalDateTime.now());
            history.setUpdateTime(LocalDateTime.now());
            this.save(history);
        });
    }
}
