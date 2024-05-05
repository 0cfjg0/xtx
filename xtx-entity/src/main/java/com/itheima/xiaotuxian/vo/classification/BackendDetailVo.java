package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class BackendDetailVo {
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 状态，0为正常，1为停用
     */
    private Integer state;
    /**
     * 层级，从1开始
     */
    private Integer layer;
    /**
     * 父级类目信息
     */
    private BackendSimpleVo parent;
    /**
     * 关键属性组集合
     */
    private List<PropertyGroupSimpleVo> crucialProperties;
    /**
     * 基础属性组集合
     */
    private List<PropertyGroupSimpleVo> baseProperties;
    /**
     * 销售属性组集合
     */
    private List<PropertyGroupSimpleVo> saleProperties;
    /**
     * 其他属性组集合
     */
    private List<PropertyGroupSimpleVo> extraProperties;
    /**
     * 品牌集合
     */
    private List<BrandSimpleVo> brands;
}
