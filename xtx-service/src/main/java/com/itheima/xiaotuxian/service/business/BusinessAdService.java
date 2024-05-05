package com.itheima.xiaotuxian.service.business;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.business.BusinessAd;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;

import java.util.List;

public interface BusinessAdService extends IService<BusinessAd> {
    /**
     * 根据查询的渠道和广告位置查询
     * @return
     * @param channel
     * @param distributionSite
     */

    /**
     * 活动的banner图，根据查询的渠道，广告位的位置和活动的id查询广告信息
     */
    List<BannerResultVo> findActivityBanner(Integer client, Integer distributionSiteActivity, String hashCode);
}
