package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class FrontRelationVo {
    /**
     * 关联标识，如新增关联无需此参数，修改则需携带此参数
     */
    private String relationKey;
    /**
     * 后台类目集合
     */
    private List<BackendSimpleVo> backends;
    /**
     * 销售属性组集合
     */
    private List<PropertyGroupSimpleVo> propertyGroups;
    /**
     * 品牌集合
     */
    private List<BrandSimpleVo> brands;
}
