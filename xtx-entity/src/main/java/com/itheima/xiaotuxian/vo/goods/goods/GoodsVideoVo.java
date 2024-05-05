package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

@Data
public class GoodsVideoVo {
    /**
     * pc视频信息
     */
    private GoodsVideoDetailVo pc;
    /**
     * app视频信息
     */
    private GoodsVideoDetailVo app;
}
