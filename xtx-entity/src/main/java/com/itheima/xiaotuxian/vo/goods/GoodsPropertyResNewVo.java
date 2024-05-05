package com.itheima.xiaotuxian.vo.goods;

import com.itheima.xiaotuxian.vo.goods.property.GoodsPropertyGroupSimpleNewVo;
import com.itheima.xiaotuxian.vo.goods.property.GoodsPropertyNewVo;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPropertyResNewVo {
    /**
     * 非销售属性集合，即基础信息中的商品属性
     */
    private List<GoodsPropertyGroupSimpleNewVo> otherProperties;
    /**
     * 销售属性集合
     */
    private List<GoodsPropertyNewVo> saleProperties;
}
