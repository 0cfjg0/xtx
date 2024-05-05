package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.util.List;

@Data
public class GoodsPictureVo {
    /**
     * pc主图片集合
     */
    private List<GoodsMaterialVo> pc;
    /**
     * app主图片集合
     */
    private List<GoodsMaterialVo> app;
}
