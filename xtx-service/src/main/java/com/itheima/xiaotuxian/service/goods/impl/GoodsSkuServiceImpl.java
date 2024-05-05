package com.itheima.xiaotuxian.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.mapper.goods.GoodsSkuMapper;
import com.itheima.xiaotuxian.service.goods.GoodsSkuService;
import com.itheima.xiaotuxian.vo.goods.goods.SkuQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lvbencai
 * @Date: 2023年4月7日08:35:19
 * @Description:
 */
@Service
@Slf4j
public class GoodsSkuServiceImpl extends ServiceImpl<GoodsSkuMapper, GoodsSku> implements GoodsSkuService {

    @Override
    public Page<GoodsSku> findByPage(Page<GoodsSku> pageResult, SkuQueryVo queryVo) {
       return baseMapper.findByPage(pageResult,queryVo);
    }

    @Override
    public List<GoodsSku> selectMinPice(String spuId) {
        return baseMapper.selectMinPice(spuId);
    }

    @Override
    public Integer selectCount(LambdaQueryWrapper<GoodsSku> eq) {
        return baseMapper.selectCount(eq);
    }

//    @Override
//    public void deleteBySpuId(LambdaQueryWrapper<GoodsSku> queryWrapper) {
//        baseMapper.delete(queryWrapper);
//    }
}
