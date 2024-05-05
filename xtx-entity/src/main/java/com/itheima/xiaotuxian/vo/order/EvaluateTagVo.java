package com.itheima.xiaotuxian.vo.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateTagVo {
    /**
     * 标签
     */
    private String title;
    /**
     * 数量
     */
    private Integer tagCount;
}
