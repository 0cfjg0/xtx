package com.itheima.xiaotuxian.service.home;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.home.HomeHotRecommend;

import java.util.List;

/**
 * <p>
 *  热门推荐的服务类
 * </p>
 *
 * @author lvbencai
 * @since 2023-05-05
 */
public interface HomeHotRecommendService extends IService<HomeHotRecommend> {

    /**
     * 根据客户端类型加载热门推荐的数据数据
     * @param client
     * @return
     */
    List<HomeHotRecommend> getList(Integer client);
}
