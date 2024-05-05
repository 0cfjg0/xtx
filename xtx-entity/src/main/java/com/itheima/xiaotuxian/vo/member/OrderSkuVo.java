package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: itheima
 * @Date: 2020/11/3 10:20 上午
 * @Description:
 */
@Data
public class OrderSkuVo {
    private String id;
    /**
     * spu id
     */
    private String spuId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 图片地址
     */
    private String image;
    /**
     * 实付金额
     */
    private BigDecimal realPay;
    /**
     * 单价
     */
    private BigDecimal curPrice;
    /**
     * 小计
     */
    private BigDecimal totalMoney;
    /**
     * 属性集合
     */
    private List<OrderSkuPropertyVo> properties;
    /**
     * 属性例如“颜色:瓷白色 尺寸：8寸”
     */
    private String attrsText;
}
