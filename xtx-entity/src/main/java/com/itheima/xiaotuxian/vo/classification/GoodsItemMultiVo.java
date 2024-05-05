package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;

@Data
public class GoodsItemMultiVo {
    /**
     * 商品分页数据
     */
    private Pager<GoodsItemResultVo> pageData;
    /**
     * 品牌集合
     */
    private List<BrandSimpleVo> brands;
}
