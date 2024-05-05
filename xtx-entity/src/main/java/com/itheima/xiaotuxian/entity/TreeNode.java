package com.itheima.xiaotuxian.entity;

import lombok.Data;

@Data
public class TreeNode {
    /**
     * id
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 父节点id
     */
    private String pid;
}
