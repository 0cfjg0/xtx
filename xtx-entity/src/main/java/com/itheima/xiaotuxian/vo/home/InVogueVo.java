package com.itheima.xiaotuxian.vo.home;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InVogueVo {
    /**
     * 24小时热销
     */
    private List<GoodsItemResultVo> byDay;
    /**
     * 人气周榜
     */
    private List<GoodsItemResultVo> byWeek;
    /**
     * 热销总榜
     */
    private List<GoodsItemResultVo> byTotal;
}
