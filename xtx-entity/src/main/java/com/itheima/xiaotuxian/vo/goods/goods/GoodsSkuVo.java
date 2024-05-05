package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class GoodsSkuVo {
    /**
     * id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品图片
     */
    private String picture;
    /**
     * 商品价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 可销售库存
     */
    private Integer saleableInventory;
    /**
     * 实际库存
     */
    private Integer physicalInventory;
    /**
     * 累计销量
     */
    private Integer salesCount;
    /**
     * 商品状态，1为出售中，2为仓库中，3为已售罄，4为回收站，5为历史宝贝
     */
    private Integer state;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    /**
     * 规格
     */
    private String specification;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * spuId
     */
    private String spuId;
}
