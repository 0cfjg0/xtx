package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class SkuVo {
    /**
     * id
     */
    private String id;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 当前价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 原价
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal oldPrice;
    /**
     * 库存
     */
    private Integer inventory;

    /**
     * sku图片
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String picture;
    /**
     * 规格集合
     */
    private List<SkuSpecVo> specs;
}
