package com.itheima.xiaotuxian.mapper.classification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClassificationFrontMapper extends BaseMapper<ClassificationFront> {
    /**
     * 获取同级最大排序数
     * @param pid 父级id
     * @return 最大排序数
     */
    Integer getMaxSort(@Param(value = "pid") String pid);
}
