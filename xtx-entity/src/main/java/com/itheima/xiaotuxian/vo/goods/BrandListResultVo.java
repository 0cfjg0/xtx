package com.itheima.xiaotuxian.vo.goods;

import lombok.Data;

import java.util.List;

@Data
public class BrandListResultVo {
    /**
     * 首字母
     */
    private String firstWord;
    /**
     * 品牌列表
     */
    private List<BrandVo> brands;
}
