package com.itheima.xiaotuxian.vo.marketing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TopicVo {
    private String id;
    /**
     * 专题分类信息
     */
    private TopicClassificationVo classification;
    /**
     * 标题
     */
    private String title;
    /**
     * 副标题
     */
    private String summary;
    /**
     * 最低价格
     */
    private BigDecimal lowestPrice;
    /**
     * 专题封面
     */
    private String cover;
    /**
     * 详情链接
     */
    private String detailsUrl;
    /**
     * 收藏数
     */
    private Long collectNum;
    /**
     * 浏览数
     */
    private Long viewNum;
    /**
     * 更新时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;
    /**
     * 是否收藏
     */
    private Boolean isCollect;
}
