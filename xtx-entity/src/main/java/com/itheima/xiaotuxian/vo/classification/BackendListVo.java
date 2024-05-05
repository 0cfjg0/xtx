package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/13 3:23 下午
 * @Description:
 */
@Data
public class BackendListVo {
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 状态，0为正常，1为停用
     */
    private Integer state;
    /**
     * 层级，从1开始
     */
    private Integer layer;
    /**
     * 商品数量
     */
    private Integer goodsCount;
    /**
     * 品牌数量
     */
    private Integer brandCount;
    /**
     * 属性组数量
     */
    private Integer propertyGroupCount;
    /**
     * 父级类目信息
     */
    private BackendSimpleVo parent;
}
