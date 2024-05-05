package com.itheima.xiaotuxian.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/9 11:00 上午
 * @Description:
 */
@Data
public class BatchOperatorVo {
    /**
     * 待操作对象id集合
     */
    private List<String> ids;
    /**
     * 状态
     */
    private Integer state;
}
