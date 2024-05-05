package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.vo.goods.goods.SkuQueryVo;

import java.util.List;

public interface GoodsSkuService extends IService<GoodsSku> {

    Page<GoodsSku> findByPage(Page<GoodsSku> pageResult, SkuQueryVo queryVo);

    List<GoodsSku> selectMinPice(String spuId);

    Integer selectCount(LambdaQueryWrapper<GoodsSku> eq);

//    void deleteBySpuId(LambdaQueryWrapper<GoodsSku> queryWrapper);
}
