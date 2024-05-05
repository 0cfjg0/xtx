package com.itheima.xiaotuxian.vo.member;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

@Data
public class BrowseHistoryVo {
    /**
     * 足迹id
     */
    private String id;
    /**
     * 商品信息
     */
    private GoodsItemResultVo spu;
}
