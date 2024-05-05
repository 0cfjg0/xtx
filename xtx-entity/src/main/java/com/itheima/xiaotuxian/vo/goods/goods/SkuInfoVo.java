package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SkuInfoVo {
    /**
     * 表格信息
     */
    private List<SkuTableVo> tables;
    /**
     * sku信息索引
     */
    private Map<String, SkuIndexVo> skuIndex;
    /**
     * 已配置销售属性
     */
    private Map<String, List<SkuSaleConfigVo>> saleConfigs;
}
