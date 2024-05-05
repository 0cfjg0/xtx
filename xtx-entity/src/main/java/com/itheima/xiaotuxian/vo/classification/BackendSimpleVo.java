package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

@Data
public class BackendSimpleVo {
    /**
     * 后台类目Id
     */
    private String id;
    /**
     * 后台类目名称
     */
    private String name;
    /**
     * 父级分类
     */
    private BackendSimpleVo parent;
}
