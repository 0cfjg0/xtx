package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

@Data
public class EsFront {
    /**
     * id
     */
    private String id;
    /**
     * 前台类目名称
     */
    private String name;
    /**
     * 层级
     */
    private Integer layer;
}
