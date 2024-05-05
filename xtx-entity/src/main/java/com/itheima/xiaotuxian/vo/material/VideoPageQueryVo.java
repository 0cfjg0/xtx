package com.itheima.xiaotuxian.vo.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VideoPageQueryVo {
    /**
     * 页码
     */
    private Integer page=1;
    /**
     * 页尺寸
     */
    private Integer pageSize=10;
    /**
     * 是否使用，0为全部，1为未使用，2为已使用
     */
    private Integer isUsed;
    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 图片组id,未分组该参数传值为0，全部图片无需传该值或空字符串
     */
    private String groupId;
    /**
     * 回收站数据
     */
    private Boolean trash = false;
    /**
     * 搜索关键字
     */
    private String keyword;
    /**
     * 商品名称关键字
     */
    private String goodsKeyword;
    /**
     * 排序字段名称,取值范围：[name,updateTime]
     */
    private String sortName;
    /**
     * 排序方法，取值范围：[正序asc，倒序desc]
     */
    private String sortMethod;
    /**
     * 0为全部，1为已冻结，2为正常
     */
    private Integer auditState;
    /**
     * 0为全部，1为手机端，2为PC端
     */
    private Integer display;
    /**
     * 隐藏不可用图片，不传或false为不隐藏
     */
//    private Boolean hideUnavailable;
}
