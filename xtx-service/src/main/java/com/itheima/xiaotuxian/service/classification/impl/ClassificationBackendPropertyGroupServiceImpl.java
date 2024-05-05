package com.itheima.xiaotuxian.service.classification.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendPropertyGroup;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendPropertyGroupMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendPropertyGroupService;
import org.springframework.stereotype.Service;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:09 下午
 * @Description:
 */
@Service
public class ClassificationBackendPropertyGroupServiceImpl extends ServiceImpl<ClassificationBackendPropertyGroupMapper, ClassificationBackendPropertyGroup> implements ClassificationBackendPropertyGroupService {
    @Override
    public Integer countPropertyGroup(String id) {
        return this.getBaseMapper().selectCount(Wrappers.<ClassificationBackendPropertyGroup>lambdaQuery().eq(ClassificationBackendPropertyGroup::getClassificationBackendId, id));
    }
}
