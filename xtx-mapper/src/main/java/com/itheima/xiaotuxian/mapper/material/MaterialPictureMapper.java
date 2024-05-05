package com.itheima.xiaotuxian.mapper.material;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.material.MaterialPicture;
import com.itheima.xiaotuxian.vo.material.PictureItemVo;
import com.itheima.xiaotuxian.vo.material.PicturePageQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MaterialPictureMapper extends BaseMapper<MaterialPicture> {
    Page<PictureItemVo> findByPage(Page<PictureItemVo> page, @Param("queryVo") PicturePageQueryVo queryVo);
}
