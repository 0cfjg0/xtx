package com.itheima.xiaotuxian.service.classification;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationFrontRelation;

import java.util.List;

public interface ClassificationFrontRelationService extends IService<ClassificationFrontRelation> {
    /**
     * 通过前台分类获取前台分类关联
     *
     * @param frontId 前台分类id
     * @return 前台分类关联集合
     */
    List<ClassificationFrontRelation> findRelationByFrontId(String frontId);

    /**
     * 通过关联关系获取前台分类关联
     *
     * @param ids 关联对象id集合
     * @return 前台分类关联集合
     */
    List<ClassificationFrontRelation> findAllByRelation(List<String> ids, Integer type);


}
