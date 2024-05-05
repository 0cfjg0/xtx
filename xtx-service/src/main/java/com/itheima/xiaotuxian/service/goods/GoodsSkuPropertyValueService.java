package com.itheima.xiaotuxian.service.goods;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsSkuPropertyValue;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsSpecItemVo;

import java.util.List;

/*
 * @author: lbc
 * @Date: 2023-04-29 18:06:03
 * @Descripttion: 
 */
public interface GoodsSkuPropertyValueService extends IService<GoodsSkuPropertyValue> {
    /**
     * @description: 
     * @param {String} spuId
     * @return {*}
     * @author: lbc
     */    
    List<GoodsSkuPropertyValue> findAllSkuPropertyValueBySpu(String spuId);

    /**
     * @description: 
     * @param {String} spuId
     * @return {*}
     * @author: lbc
     */    
    List<GoodsSpecItemVo> findDistinctSpecsValueBySpu(String spuId,String client);
}
