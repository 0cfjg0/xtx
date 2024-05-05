package com.itheima.xiaotuxian.mapper.property;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.property.PropertyMain;
import com.itheima.xiaotuxian.vo.property.PropertyQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PropertyMainMapper extends BaseMapper<PropertyMain> {
    Page<PropertyMain> findByPage(Page<PropertyMain> page, @Param("queryVo") PropertyQueryVo queryVo);
}
