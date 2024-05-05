package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

import java.util.List;

@Data
public class BackendSaveVo {
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父级类目信息
     */
    private BackendSimpleVo parent;
    /**
     * 关键属性组集合
     */
    private List<String> crucialProperties;
    /**
     * 基础属性组集合
     */
    private List<String> baseProperties;
    /**
     * 销售属性组集合
     */
    private List<String> saleProperties;
    /**
     * 其他属性组集合
     */
    private List<String> extraProperties;
    /**
     * 品牌集合
     */
    private List<String> brands;
}
