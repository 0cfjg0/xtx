package com.itheima.xiaotuxian.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "marketing_topic")
public class MarketingTopic extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 专题分类id
     */
    private String classificationId;
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
     * 回复数
     */
    private Long replyNum;
}
