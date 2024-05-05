package com.itheima.xiaotuxian.service.classification;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendBrand;

import java.util.List;

public interface ClassificationBackendBrandService extends IService<ClassificationBackendBrand> {
    /**
     * 根据条件获取后台类目与品牌关联列表
     *
     * @param backendId 后台类目id
     * @return 后台类目与品牌关联列表
     */
    List<ClassificationBackendBrand> findAllBackendBrand(String backendId, String brandId);

}