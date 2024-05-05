package com.itheima.xiaotuxian.vo.search;

import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

@Data
public class SearchGoodsVo {
    /**
     * 条件信息
     */
    private SearchConditionVo conditions;
    /**
     * 商品集合
     */
    private Pager<GoodsItemResultVo> pageData;
}
