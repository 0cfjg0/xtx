package com.itheima.xiaotuxian.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.xiaotuxian.vo.member.EvaluateMemberSimpleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderEvaluateVo {
    /**
     * id
     */
    private String id;
    /**
     * 订单信息
     */
    private EvaluateOrderInfoVo orderInfo;
    /**
     * 评价用户信息
     */
    private EvaluateMemberSimpleVo member;
    /**
     * 评分，取值范围0-5
     */
    private BigDecimal score;
    /**
     * 印象标签集合
     */
    private List<String> tags;
    /**
     * 评论正文
     */
    private String content;
    /**
     * 评论晒图集合
     */
    private List<String> pictures;
    /**
     * 客服回复
     */
    private String officialReply;
    /**
     * 点赞数
     */
    private Integer praiseCount;
    /**
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 好评率
     */
    private BigDecimal praisePercent;
}
