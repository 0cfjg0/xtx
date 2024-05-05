package com.itheima.xiaotuxian.vo;

import com.itheima.xiaotuxian.entity.TreeNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNodeVo extends TreeNode {
    /**
     * 子节点集合
     */
    private List<TreeNodeVo> children=new ArrayList<>();
}
