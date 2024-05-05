package com.itheima.xiaotuxian.service.business.impl;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 
 */

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.business.BusinessAd;
import com.itheima.xiaotuxian.mapper.business.BusinessAdMapper;
import com.itheima.xiaotuxian.service.business.BusinessAdService;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itheima
 * @Date: 2023/7/10 4:51 下午
 * @Description:
 */
@Service
public class BusinessAdServiceImpl extends ServiceImpl<BusinessAdMapper, BusinessAd> implements BusinessAdService {

    /**
     * 获取Banner
     *
     * @return Banner集合
     * @param channel
     * @param distributionSite
     */




    @Override
    /**
     * @description: 
     * @param {Integer} client
     * @param {Integer} distributionSiteActivity
     * @param {String} hashCode
     * @return {*}
     * @author: lbc
     */
    public List<BannerResultVo> findActivityBanner(Integer client, Integer distributionSiteActivity, String hashCode) {
        var banners =  this.list(Wrappers.<BusinessAd>lambdaQuery()
                .eq(ObjectUtil.isEmpty(client), BusinessAd::getDistributionChannel, client)
                .eq(BusinessAd::getDistributionSite, distributionSiteActivity)
                .eq(BusinessAd::getClassificationFrontId, hashCode)
                .orderByDesc(BusinessAd::getUpdateTime).last("LIMIT 5"))
                .stream().map(businessAd -> {
                    var vo = new BannerResultVo();
                    vo.setId(businessAd.getId());
                    vo.setImgUrl(businessAd.getBannerUrl());
                    vo.setHrefUrl(businessAd.getTargetUrl());
                    vo.setType(businessAd.getTargetType());
                    return vo;
        }).collect(Collectors.toList());
        return banners;
    }
}
