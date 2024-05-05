package com.itheima.xiaotuxian.service.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.entity.marketing.MarketingOneStop;
import com.itheima.xiaotuxian.entity.marketing.MarketingOneStopSpu;
import com.itheima.xiaotuxian.mapper.marketing.MarketingOneStopMapper;
import com.itheima.xiaotuxian.mapper.marketing.MarketingOneStopSpuMapper;
import com.itheima.xiaotuxian.service.marketing.MarketingOneStopService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.marketing.OneStopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MarketingOneStopServiceImpl extends ServiceImpl<MarketingOneStopMapper, MarketingOneStop> implements MarketingOneStopService {
    @Autowired
    private MarketingOneStopSpuMapper oneStopSpuMapper;
    @Autowired
    private SearchGoodsService searchGoodsService;

    @Override
    public List<OneStopVo> findAll(String client) {
        return list().stream().map(oneStop -> {
            var result = BeanUtil.toBean(oneStop, OneStopVo.class);
            //获取商品信息
            var goodIds = oneStopSpuMapper.selectList(Wrappers.<MarketingOneStopSpu>lambdaQuery()
                    .eq(MarketingOneStopSpu::getOneStopId, oneStop.getId())
            ).stream().map(MarketingOneStopSpu::getSpuId).distinct().collect(Collectors.toList());
            Stream.of(goodIds).filter(CollUtil::isNotEmpty).forEach(ids -> {
                var queryVo = new GoodsQueryPageVo();
                queryVo.setIds(ids);
                result.setGoods(searchGoodsService.findAllGoods(queryVo).stream().map(esGoods -> {
                    var goodsResult = BeanUtil.toBean(esGoods, GoodsItemResultVo.class);
                    goodsResult.setPicture(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? esGoods.getPcPicture() : esGoods.getAppPicture());
                    goodsResult.setDesc(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? esGoods.getPcDecription() : esGoods.getAppDecription());
                    return goodsResult;
                }).collect(Collectors.toList()));
            });
            return result;
        }).collect(Collectors.toList());
    }
}
