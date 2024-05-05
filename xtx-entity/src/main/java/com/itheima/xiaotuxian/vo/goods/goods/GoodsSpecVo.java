package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.util.List;

@Data
public class GoodsSpecVo {
    /**
     * 可选规格集合
     */
    private List<GoodsSpecItemVo> specs;
    /**
     * sku集合
     */
    private List<SkuVo> skus;
}
