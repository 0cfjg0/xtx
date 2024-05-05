package com.itheima.xiaotuxian.vo.search;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:38
 * @Descripttion:
 */

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchQueryVo {
    /**
     * 页码
     */
    private Integer page = 1;
    /**
     * 页尺寸
     */
    private Integer pageSize = 10;
    /**
     * 所输入的关键词
     */
    private String keyword;
    /**
     * 联想词Id集合
     */
    private List<String> associatedIds;
    /**
     * 分类id
     */
    private String categoryId;
    /**
     * 前台分类ids(和分类id对应同一字段)
     */
    private List<String> frontIds;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 只显示特惠
     */
    private Boolean onlyDiscount;
    /**
     * 排序字段，取值范围：[publishTime,orderNum,price,evaluateNum]
     */
    private String sortField;
    /**
     * 排序规则，asc为正序，desc为倒序，默认为desc
     */
    private String sortMethod = "desc";
    /**
     * 最低价
     */
    private BigDecimal lowPrice;
    /**
     * 最高价
     */
    private BigDecimal highPrice;
    /**
     * 是否有库存
     */
    private Boolean inventory;
    /**
     * 属性条件数组
     */
    private List<SearchPropertyVo> attrs;
}
