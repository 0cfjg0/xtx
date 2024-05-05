package com.itheima.xiaotuxian.vo.goods.goods;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class GoodsItemResultVo  implements Serializable {
    /**
     * spuId,即商品Id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String desc;
    /**
     * 商品价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 商品图片
     */
    private String picture;
    /**
     * 折扣信息，如为null时，即无折扣
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal discount;
    /**
     * 销量
     */
    private Integer orderNum;
}
