package com.itheima.xiaotuxian.service.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.marketing.MarketingOneStop;
import com.itheima.xiaotuxian.vo.marketing.OneStopVo;

import java.util.List;

public interface MarketingOneStopService extends IService<MarketingOneStop> {
    List<OneStopVo> findAll(String client);
}
