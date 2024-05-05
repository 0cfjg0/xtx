package com.itheima.xiaotuxian.service.goods.impl;
/*
 * @author: lbc
 * @Date: 2023-04-29 18:06:03
 * @Descripttion:
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsSkuPropertyValue;
import com.itheima.xiaotuxian.mapper.goods.GoodsSkuPropertyValueMapper;
import com.itheima.xiaotuxian.service.goods.GoodsSkuPropertyValueService;
import com.itheima.xiaotuxian.service.goods.GoodsSkuService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsSpecItemVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsSpecValueVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: lvbencai
 * @Date: 2023年4月22日11:32:02
 * @Description:
 */
@Service
@Slf4j
public class GoodsSkuPropertyValueServiceImpl extends ServiceImpl<GoodsSkuPropertyValueMapper, GoodsSkuPropertyValue> implements GoodsSkuPropertyValueService {
    @Autowired
    GoodsSkuService skuService;
    @Autowired
    MaterialPictureService materialPictureService;
    @Value("${picture.head.resize:?quality=95&imageView}")
    private String headResize;
    @Override
    public List<GoodsSkuPropertyValue> findAllSkuPropertyValueBySpu(String spuId) {
        var results = new ArrayList<GoodsSkuPropertyValue>();
        var skuPropertyValues = list(Wrappers.<GoodsSkuPropertyValue>lambdaQuery()
                            .in(GoodsSkuPropertyValue::getSpuId, spuId)
                            .orderByDesc(GoodsSkuPropertyValue::getSkuId,GoodsSkuPropertyValue::getPropertyMainName));
        if(CollUtil.isNotEmpty(skuPropertyValues)){
            results.addAll(skuPropertyValues);
        }
        return results;
    }

    @Override
    public List<GoodsSpecItemVo> findDistinctSpecsValueBySpu(String spuId,String client) {
        QueryWrapper<GoodsSkuPropertyValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT `property_main_name`", "property_main_id")
                .eq("spu_id",spuId).orderByDesc("property_main_name");

        List<GoodsSkuPropertyValue>  values =this.list(queryWrapper);

        List<GoodsSpecItemVo> vos = new ArrayList<>();
        Optional.ofNullable(values).filter(CollUtil::isNotEmpty)
                .ifPresent(valueList->{
                        vos.addAll(valueList.stream().map(value->{
                            var vo = new GoodsSpecItemVo();
                            vo.setId(value.getPropertyMainId());
                            vo.setName(value.getPropertyMainName());
                            //联合查询出图片 关联查询出图片信息还是单表查如询  那个更快呢？ 目前数据量不多，如何考虑
                            // 如果图片表的数据达到了百万和千万的量级 关联查询方便么 如果图片分表了，逻辑可能更复杂了
                            QueryWrapper<GoodsSkuPropertyValue> queryValueWrapper = new QueryWrapper<>();
                            queryValueWrapper.select("DISTINCT `property_value_name`", "property_value_description", "property_value_picture_id")
                                    .eq("spu_id",spuId).eq("property_main_id",vo.getId());
                            Optional.ofNullable(this.list(queryValueWrapper))
                                    .filter(CollUtil::isNotEmpty)
                                    .ifPresent(skuPropertyValues -> {
                                        vo.setValues(
                                            skuPropertyValues.stream().distinct().collect(Collectors.toList()).stream().map(skuPropertyValue->{
                                                var valueVo = new GoodsSpecValueVo();
                                                valueVo.setName(skuPropertyValue.getPropertyValueName());
                                                valueVo.setDesc(skuPropertyValue.getPropertyValueDescription());
                                                Optional.ofNullable(materialPictureService.getById(skuPropertyValue.getPropertyValuePictureId()))
                                                        .ifPresent(picture->{
                                                            if(CommonStatic.REQUEST_CLIENT_PC.equals(client)){
                                                                valueVo.setPicture(picture.getUrl());
                                                            }else{
                                                                valueVo.setPicture(picture.getUrl()+ headResize);
                                                            }
                                                        });
                                                return  valueVo;
                                            }).collect(Collectors.toList())
                                        );
                                    });
                            return vo;
                    }).collect(Collectors.toList()));
                });
        return vos;
    }
}
