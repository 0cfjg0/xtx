package com.itheima.xiaotuxian.vo.classification;

import com.itheima.xiaotuxian.vo.material.MaterialPictureVo;
import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/13 3:23 下午
 * @Description:
 */
@Data
public class ClassificationFrontVo {
    private String id;
    /**
     * 前台分类名称
     */
    private String name;
    /**
     * 分类图片
     */
    private MaterialPictureVo picture;
    /**
     * 父级分类id
     */
    private ClassificationFrontVo parent;
    /**
     * 子分类集合
     */
    private List<ClassificationFrontVo> children;
    /**
     * 条件关联集合
     */
    private List<BackendSimpleVo> backends;
    /**
     * id集合
     */
    private List<String> ids;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否已使用
     */
    private Boolean useState;
}
