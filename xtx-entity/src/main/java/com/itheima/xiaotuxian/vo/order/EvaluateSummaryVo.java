package com.itheima.xiaotuxian.vo.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateSummaryVo {
    /**
     * 销量
     */
    private Integer salesCount;
    /**
     * 好评率
     */
    private BigDecimal praisePercent;
    /**
     * 评价数
     */
    private Integer evaluateCount;
    /**
     * 评价有图数
     */
    private Integer hasPictureCount;
    /**
     * 标签统计信息集合
     */
    private List<EvaluateTagVo> tags;
}
