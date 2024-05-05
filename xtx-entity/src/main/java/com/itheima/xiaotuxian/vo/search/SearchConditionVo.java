package com.itheima.xiaotuxian.vo.search;

import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchConditionVo {
    /**
     * 分类集合
     */
    private List<FrontSimpleVo> categories;
    /**
     * 品牌集合
     */
    private List<BrandSimpleVo> brands;
}
