package com.itheima.xiaotuxian.vo.goods;

import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class BrandConditionVo {
    /**
     * 产地集合
     */
    private List<String> productionPlaces;
    /**
     * 首字母集合
     */
    private List<String> firstWords;
    /**
     * 分类集合
     */
    private List<FrontSimpleVo> categories;
}
