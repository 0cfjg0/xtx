package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class FrontSaveVo {
    private String id;
    /**
     * 前台分类名称
     */
    private String name;
    /**
     * 分类图片
     */
    private PictureSimpleVo picture;
    /**
     * 父级分类
     */
    private FrontSimpleVo parent;
    /**
     * 关联关系集合
     */
    private List<FrontRelationSaveVo> relations;
    /**
     * 排序，数值越大越靠前
     */
    private Integer sort;
    /**
     * 是否启用，0为启用、1为未启用
     */
    private Integer state;
}
