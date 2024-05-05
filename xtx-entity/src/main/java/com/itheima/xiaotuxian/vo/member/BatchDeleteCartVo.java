package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/9 11:00 上午
 * @Description:
 */
@Data
public class BatchDeleteCartVo {
    /**
     * 待删除对象id集合
     */
    private List<String> ids;
    /**
     * 是否全部清除
     */
    private Boolean clearAll = false;
    /**
     * 对象类型
     */
    private Boolean clearInvalid = false;
}
