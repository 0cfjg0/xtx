package com.itheima.xiaotuxian.service.marketing.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.marketing.MarketingRecommend;
import com.itheima.xiaotuxian.mapper.marketing.MarketingRecommendMapper;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import org.springframework.stereotype.Service;

@Service
public class MarketingRecommendServiceImpl extends ServiceImpl<MarketingRecommendMapper, MarketingRecommend> implements MarketingRecommendService {
    @Override
    public MarketingRecommend findOne(String spuId, Integer type) {
        return getOne(Wrappers.<MarketingRecommend>lambdaQuery()
                .eq(MarketingRecommend::getSpuId, spuId)
                .eq(MarketingRecommend::getRecommendType, type)
        );
    }
}
