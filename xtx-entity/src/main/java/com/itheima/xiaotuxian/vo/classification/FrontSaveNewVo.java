package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class FrontSaveNewVo {
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
    private List<BackendSimpleRelationVo> relations;

}
