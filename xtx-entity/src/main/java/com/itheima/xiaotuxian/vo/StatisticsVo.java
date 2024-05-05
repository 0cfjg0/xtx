package com.itheima.xiaotuxian.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsVo {
    /**
     * 回收站数量
     */
    private Integer numWithRecycle;
    /**
     * 草稿箱数量
     */
    private Integer numWithEdit;
}
