package com.itheima.xiaotuxian.service.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.marketing.MarketingRecommend;

public interface MarketingRecommendService extends IService<MarketingRecommend> {
    MarketingRecommend findOne(String spuId,Integer type);
}
