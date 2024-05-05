package com.itheima.xiaotuxian.vo.order;

import lombok.Data;

@Data
public class EvaluateQueryVo {
    /**
     * 页码
     */
    private Integer page = 1;
    /**
     * 页尺寸
     */
    private Integer pageSize = 10;
    /**
     * 是否有图
     */
    private Boolean hasPicture;
    /**
     * 标签
     */
    private String tag;
    /**
     * 排序字段，可选值范围[praiseCount,createTime]
     */
    private String sortField;
    /**
     * 排序方法，可选值范围[asc,desc],默认为desc
     */
    private String sortMethod;
    /**
     * 商品id
     */
    private String spuId;
}
