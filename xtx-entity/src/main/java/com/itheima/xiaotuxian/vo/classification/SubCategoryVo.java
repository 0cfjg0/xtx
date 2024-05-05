package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.property.PropertyMainVo;
import lombok.Data;

import java.util.List;

@Data
public class SubCategoryVo {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String picture;
    /**
     * 顶级分类ID
     */
    private String parentId;
    /**
     * 顶级分类名称
     */
    private String parentName;
    /**
     * 商品集合
     */
    private List<GoodsItemResultVo> goods;
    /**
     * 分类集合
     */
    private List<FrontSimpleVo> categories;
    /**
     * 品牌集合
     */
    private List<BrandSimpleVo> brands;
    /**
     * 销售属性集合
     *  2023-04-26  修改此字段的返回泛型 后续测试是否能够适应所有的接口
     */
    private List<PropertyMainVo> saleProperties;
}
