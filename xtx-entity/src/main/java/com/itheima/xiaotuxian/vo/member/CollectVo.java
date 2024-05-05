package com.itheima.xiaotuxian.vo.member;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class CollectVo {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 图片
     */
    private String picture;
    /**
     * 收藏类型，1为商品，2为专题，3为品牌
     */
    private Integer collectType;
    /**
     * 商品-商品价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
    /**
     * 商品-折扣信息
     */
    private BigDecimal discount;
    /**
     * 品牌-产地，多字段通过/分割
     */
    private String productionPlace;
    /**
     * 详情链接
     */
    private String detailsUrl;
}
