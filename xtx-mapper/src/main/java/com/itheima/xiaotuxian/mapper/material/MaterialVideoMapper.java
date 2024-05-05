package com.itheima.xiaotuxian.mapper.material;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.material.MaterialVideo;
import com.itheima.xiaotuxian.vo.material.VideoItemVo;
import com.itheima.xiaotuxian.vo.material.VideoPageQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MaterialVideoMapper extends BaseMapper<MaterialVideo> {
    Page<VideoItemVo> findByPage(Page<VideoItemVo> page, @Param("queryVo") VideoPageQueryVo queryVo);
}
