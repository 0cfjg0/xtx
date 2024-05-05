package com.itheima.xiaotuxian.mapper.property;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.property.PropertyGroup;
import com.itheima.xiaotuxian.vo.property.PropertyGroupQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyGroupMapper extends BaseMapper<PropertyGroup> {
    /**
     * 获取全部可用属性组
     *
     * @param queryVo 查询条件
     * @return 全部可用属性组
     */
    List<PropertyGroup> findAll(@Param("queryVo") PropertyGroupQueryVo queryVo);
}
