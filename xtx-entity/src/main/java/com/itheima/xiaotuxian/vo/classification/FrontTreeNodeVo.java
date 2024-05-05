package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

import java.util.List;

@Data
public class FrontTreeNodeVo {
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 子级类目集合
     */
    private List<FrontTreeNodeVo> children;
}
