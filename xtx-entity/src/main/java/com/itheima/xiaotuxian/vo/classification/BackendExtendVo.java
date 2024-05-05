package com.itheima.xiaotuxian.vo.classification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupVo;
import lombok.Data;

import java.util.List;

@Data
public class BackendExtendVo {
    /**
     * 后台类目Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    /**
     * 后台类目名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    /**
     * 关键属性组集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertyGroupVo> crucialProperties;
    /**
     * 基础属性组集合
     */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertyGroupVo> baseProperties;
    /**
     * 销售属性组集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertyGroupVo> saleProperties;
    /**
     * 其他属性组集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertyGroupVo> extraProperties;
    /**
     * 品牌集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BrandSimpleVo> brands;
}
