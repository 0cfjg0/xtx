package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

@Data
public class FrontPageQueryVo {
    /**
     * 分类名称
     */
    private String name;
    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 页尺寸
     */
    private Integer pageSize;
}
