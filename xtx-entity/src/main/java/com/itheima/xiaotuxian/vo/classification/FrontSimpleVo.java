package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

@Data
public class FrontSimpleVo{
    /**
     * 前台类目Id
     */
    private String id;
    /**
     * 前台类目名称
     */
    private String name;
    /**
     * 层级，从1开始
     */
    private Integer layer;
    /**
     * 父级分类信息
     */
    private FrontSimpleVo parent;
}
