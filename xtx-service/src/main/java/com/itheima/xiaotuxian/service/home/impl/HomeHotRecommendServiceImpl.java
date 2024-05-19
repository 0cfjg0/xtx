package com.itheima.xiaotuxian.service.home.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.entity.home.HomeHotRecommend;
import com.itheima.xiaotuxian.mapper.home.HomeHotRecommendMapper;
import com.itheima.xiaotuxian.service.home.HomeHotRecommendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lvbencai
 * @since 2023-05-05
 */
@Service
public class HomeHotRecommendServiceImpl extends ServiceImpl<HomeHotRecommendMapper, HomeHotRecommend> implements HomeHotRecommendService {

    @Override
    public List<HomeHotRecommend> getList(Integer client) {
        var list= this.baseMapper.selectList(Wrappers.<HomeHotRecommend>lambdaQuery()
                .eq(HomeHotRecommend::getDistributionChannel,client)
                .orderByAsc(HomeHotRecommend::getType));
        return list;
    }
}