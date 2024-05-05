package com.itheima.xiaotuxian.util;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.itheima.xiaotuxian.entity.TreeNode;

import java.util.List;

public class TreeDataUtil {
    private TreeDataUtil() {
    }

    public static List<Tree<String>> getTreeData(List<TreeNode> treeNodes, Integer deep) {
        //配置
        var treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("id");
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setParentIdKey("pid");
        treeNodeConfig.setChildrenKey("children");
        // 最大递归深度
        treeNodeConfig.setDeep(deep);

        //转换器
        return TreeUtil.build(treeNodes, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getPid());
                    tree.setWeight(treeNode.getId());
                    tree.setName(treeNode.getName());
                });
    }
}
