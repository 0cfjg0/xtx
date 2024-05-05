package com.itheima.xiaotuxian.entity.mq;

import lombok.Data;

@Data
public class OperateMessage {
    /**
     * 操作对象id
     */
    private String id;
    /**
     * 操作类型
     */
    private String opType;
}
