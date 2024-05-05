package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

@Data
public class FrontSimpleRelationVo {
    /**
     * 前台类目Id
     */
    private String id;
    /**
     * 前台类目名称
     */
    private String name;

    /**
     * 父级分类信息
     */
    private FrontSimpleRelationVo parent;
}
