package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.util.List;

@Data
public class GoodsPictureSaveVo {
    /**
     * pc图片id集合
     */
    private List<String> pc;
    /**
     * app图片id集合
     */
    private List<String> app;
}
