package com.itheima.xiaotuxian.mapper.home;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.home.HomeHotRecommend;
import com.itheima.xiaotuxian.vo.home.response.HotRecommendVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lvbencai
 * @since 2023-05-05
 */
@Mapper
public interface HomeHotRecommendMapper extends BaseMapper<HomeHotRecommend> {

    @Select("select id,picture_left as picture,title,alt from home_hot_recommend limit 0,4")
    List<HotRecommendVo> getHot();

}
