package com.itheima.xiaotuxian.service.marketing.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.marketing.MarketingTopic;
import com.itheima.xiaotuxian.mapper.marketing.MarketingTopicMapper;
import com.itheima.xiaotuxian.service.marketing.MarketingTopicService;
import org.springframework.stereotype.Service;

@Service
public class MarketingTopicServiceImpl extends ServiceImpl<MarketingTopicMapper, MarketingTopic> implements MarketingTopicService {
    @Override
    public Page<MarketingTopic> findByPage(Integer page, Integer pageSize, String classificationId, String sortField, String sortRule) {
        var ipage = new Page(page, pageSize);
        var queryWrapper = new QueryWrapper<MarketingTopic>();
        if (StrUtil.isNotEmpty(classificationId)) {
            queryWrapper.eq(StrUtil.toUnderlineCase("classificationId"), classificationId);
        }
        if ("asc".equals(sortRule)) {
            queryWrapper.orderByAsc(StrUtil.toUnderlineCase(sortField));
        } else {
            queryWrapper.orderByDesc(StrUtil.toUnderlineCase(sortField));
        }
        return page(ipage, queryWrapper);
    }
}
