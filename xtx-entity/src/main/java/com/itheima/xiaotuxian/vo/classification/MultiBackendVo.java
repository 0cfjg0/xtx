package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

@Data
public class MultiBackendVo {
    /**
     * 一级后台分类信息
     */
    private BackendExtendVo level1;
    /**
     * 二级后台分类信息
     */
    private BackendExtendVo level2;
    /**
     * 三级后台分类信息
     */
    private BackendExtendVo level3;
}
