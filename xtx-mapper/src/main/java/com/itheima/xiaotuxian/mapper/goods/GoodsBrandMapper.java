package com.itheima.xiaotuxian.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.entity.goods.GoodsBrand;
import com.itheima.xiaotuxian.vo.goods.brand.BrandQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsBrandMapper extends BaseMapper<GoodsBrand> {
    /**
     * 获取全部产地-国家维度
     *
     * @return 全部产地
     */
    List<String> getAllProductionPlace();

    /**
     * 获取全部首字母
     *
     * @return
     */
    List<String> getAllFirstWord();

    /**
     * 获取全部前台分类
     *
     * @return
     */
    List<ClassificationFront> getAllFront();

    /**
     * 获取分页数据
     *
     * @param page    分页配置
     * @param queryVo 查询条件
     * @return 分页数据
     */
    Page<GoodsBrand> findByPage(Page<GoodsBrand> page, @Param("queryVo") BrandQueryVo queryVo);

    /**
     * 获取品牌列表
     *
     * @param queryVo 查询条件
     * @return 品牌列表
     */
    List<GoodsBrand> findAll(@Param("queryVo") BrandQueryVo queryVo);
}
