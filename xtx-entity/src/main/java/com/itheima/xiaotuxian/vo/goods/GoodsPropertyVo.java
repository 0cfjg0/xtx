package com.itheima.xiaotuxian.vo.goods;

import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyVo;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPropertyVo {
    /**
     * 非销售属性集合，即基础信息中的商品属性
     */
    private List<PropertyGroupSimpleVo> otherProperties;
    /**
     * 销售属性集合
     */
    private List<PropertyVo> saleProperties;
}
