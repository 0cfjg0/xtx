package com.itheima.xiaotuxian.vo.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import lombok.Data;

@Data
public class SearchGoodsServiceVo {
    /**
     * 条件信息
     */
    private SearchConditionVo conditions;
    /**
     * 商品集合
     */
    private Page<EsGoods> pageData;
}
