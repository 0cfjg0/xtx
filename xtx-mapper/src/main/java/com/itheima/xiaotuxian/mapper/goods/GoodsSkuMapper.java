package com.itheima.xiaotuxian.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.SkuQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsSkuMapper extends BaseMapper<GoodsSku> {
    Page<GoodsSku> findByPage(Page<GoodsSku> page,@Param("queryVo") SkuQueryVo queryVo);

    List<GoodsSku> selectMinPice(String id);


}
