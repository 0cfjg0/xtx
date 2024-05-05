package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import java.util.List;

@Data
public class MaterialMoveVo {
    /**
     * 目标素材组id,不传或空字符串则视为移动至根目录
     */
    private String targetId;
    /**
     * 操作对象信息
     */
    private MaterialOperatorVo item;
    /**
     * 操作对象集合
     */
    private List<MaterialOperatorVo> items;
}
