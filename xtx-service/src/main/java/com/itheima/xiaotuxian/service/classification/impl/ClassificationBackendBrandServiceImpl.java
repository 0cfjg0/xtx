package com.itheima.xiaotuxian.service.classification.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendBrand;
import com.itheima.xiaotuxian.mapper.classification.ClassificationBackendBrandMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendBrandService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:09 下午
 * @Description:
 */
@Service
public class ClassificationBackendBrandServiceImpl extends ServiceImpl<ClassificationBackendBrandMapper, ClassificationBackendBrand> implements ClassificationBackendBrandService {
    /**
     * 根据后台类目id获取所有的类目品牌关联信息
     * @param backendId 后台类目id
     * @param brandId
     * @return
     */
    @Override
    public List<ClassificationBackendBrand> findAllBackendBrand(String backendId, String brandId) {
        boolean isNull = backendId == null && brandId == null;
        var results = new ArrayList<ClassificationBackendBrand>();
        Stream.of(isNull).filter(Boolean.FALSE::equals).forEach(in ->
                results.addAll(baseMapper.selectList(Wrappers.<ClassificationBackendBrand>lambdaQuery()
                        .eq(StrUtil.isNotEmpty(backendId), ClassificationBackendBrand::getClassificationBackendId, backendId)
                        .eq(StrUtil.isNotEmpty(brandId), ClassificationBackendBrand::getBrandId, brandId)
                        .eq(ClassificationBackendBrand::getIsDelete, 0)
                )));
        return results;
    }
}
