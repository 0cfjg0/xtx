package com.itheima.xiaotuxian.vo.goods.goods;


import lombok.Data;

import java.util.List;

/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */
@Data
public class GoodsQueryPageVo {
    /**
     * id
     */
    private String id;
    /**
     * id集合
     */
    private List<String> ids;
    /**
     * 需排除的id集合
     */
    private List<String> disIds;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 后台类目id
     */
    private String backendId;
    /**
     * 前台类目id
     */
    private String frontId;
    /**
     * 最低价格
     */
    private Integer minPrice;
    /**
     * 最高价格
     */
    private Integer maxPrice;
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
     * 商品审核状态，1为待审核，2为审核通过，3为驳回
     */
    private Integer auditState;
    /**
     * 编辑状态，0为草稿，1为提交
     */
    private Integer editState;
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
    /**
     * 最大惩罚项，用于条件过滤后，无效参数时使用，对应字段为id
     */
    private String ban;
}
