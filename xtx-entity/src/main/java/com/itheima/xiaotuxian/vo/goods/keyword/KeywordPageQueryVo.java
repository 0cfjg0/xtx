package com.itheima.xiaotuxian.vo.goods.keyword;

import lombok.Data;

@Data
public class KeywordPageQueryVo {
    /**
     * 关键词
     */
    private String title;
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 页尺寸
     */
    private Integer pageSize;
    /**
     * 排序字段名称,取值范围：[state,createTime]
     */
    private String sortName;
    /**
     * 排序方法，取值范围：[正序asc，倒序desc]
     */
    private String sortMethod;
}
