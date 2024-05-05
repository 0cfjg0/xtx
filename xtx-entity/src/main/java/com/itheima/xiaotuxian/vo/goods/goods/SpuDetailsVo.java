package com.itheima.xiaotuxian.vo.goods.goods;

import com.itheima.xiaotuxian.vo.property.PropertySimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class SpuDetailsVo {
    /**
     * 商品详情图片集合
     */
    private List<String> pictures;
    /**
     * 商品属性集合
     */
    private List<PropertySimpleVo> properties;
}
