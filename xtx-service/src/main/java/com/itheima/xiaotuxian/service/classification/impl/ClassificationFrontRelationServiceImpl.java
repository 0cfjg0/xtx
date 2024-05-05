package com.itheima.xiaotuxian.service.classification.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.classification.ClassificationFrontRelation;
import com.itheima.xiaotuxian.mapper.classification.ClassificationFrontRelationMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendBrandService;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:15 下午
 * @Description:
 */

@Service
public class ClassificationFrontRelationServiceImpl extends ServiceImpl<ClassificationFrontRelationMapper, ClassificationFrontRelation> implements ClassificationFrontRelationService {
   @Resource
   ClassificationFrontRelationMapper  frontRelationMapper;
    @Resource
    ClassificationBackendService backendService;
    @Resource
    ClassificationBackendBrandService backendBrandService;
    /**
     *
     * @param ids 关联对象id集合
     * @param type
     * @return
     */
   @Override
   public List<ClassificationFrontRelation> findAllByRelation(List<String> ids, Integer type) {
        return frontRelationMapper.selectList(Wrappers.<ClassificationFrontRelation>lambdaQuery()
                .in(ClassificationFrontRelation::getObjectId, ids)
                .eq(ClassificationFrontRelation::getObjectType, type));
    }


    /**
     * 根据前台类目找到关联的数据
     * @param frontId 前台分类id
     * @return
     */
    @Override
    public List<ClassificationFrontRelation> findRelationByFrontId(String frontId) {
        return frontRelationMapper.selectList(Wrappers.<ClassificationFrontRelation>lambdaQuery()
                .eq(ClassificationFrontRelation::getFrontId, frontId));
    }
}
