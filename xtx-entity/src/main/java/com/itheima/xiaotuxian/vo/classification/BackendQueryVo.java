package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

import java.util.Collection;

@Data
public class BackendQueryVo {
    /**
     * 父分类
     */
    private String pid;
    /**
     * 分类Id集合
     */
    private Collection<String> ids;
    /**
     * 状态，0为正常，1为停用
     */
    private Integer state;
    /**
     * 是否需要品牌
     */
    private Boolean needBrand;
}
