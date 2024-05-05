package com.itheima.xiaotuxian.vo.home;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;

@Data
public class NewGoodsVo {
    /**
     * 力荐新品
     */
    private List<GoodsItemResultVo> highlys;
    /**
     * 新品列表
     */
    private List<GoodsItemResultVo> newProduct;
    /**
     * 预售列表
     */
    private List<GoodsItemResultVo> preSales;
}
