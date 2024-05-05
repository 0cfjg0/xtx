package com.itheima.xiaotuxian.service.classification;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendPropertyGroup;

public interface ClassificationBackendPropertyGroupService extends IService<ClassificationBackendPropertyGroup> {
    /**
     * 统计关联属性组数量
     *
     * @param id 后台类目Id
     * @return 数量
     */
    Integer countPropertyGroup(String id);

}