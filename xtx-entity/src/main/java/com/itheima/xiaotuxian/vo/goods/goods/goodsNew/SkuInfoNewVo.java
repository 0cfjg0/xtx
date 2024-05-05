package com.itheima.xiaotuxian.vo.goods.goods.goodsNew;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsSkuSaveVo;
import com.itheima.xiaotuxian.vo.goods.goods.SkuIndexVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SkuInfoNewVo {
    /**
     * 表格信息
     */
    private List<GoodsSkuSaveVo> saleProperties;
    /**
     * sku信息索引
     */
    private Map<String, SkuIndexVo> skuIndex;
    /**
     * 已配置销售属性
     */
    private Map<String, List<SkuSaleConfigNewVo>> saleConfigs;
}
