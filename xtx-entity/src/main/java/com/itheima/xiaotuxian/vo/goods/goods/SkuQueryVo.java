package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

@Data
public class SkuQueryVo {
    /**
     * 商品名称
     */
    private String name;
    /**
     * 后台类目id
     */
    private String backendId;
    /**
     * 最小销量
     */
    private Integer minSalesCount;
    /**
     * 最大销量
     */
    private Integer maxSalesCount;
    /**
     * 商品状态，1为出售中，2为仓库中，3为已售罄
     */
    private Integer state;
    /**
     * 当前页码
     */
    private Integer page = 1;
    /**
     * 页尺寸
     */
    private Integer pageSize = 10;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
}
