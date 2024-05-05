package com.itheima.xiaotuxian.service.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.order.OrderSkuEvaluate;
import com.itheima.xiaotuxian.vo.order.EvaluateQueryVo;
import com.itheima.xiaotuxian.vo.order.OrderEvaluateVo;

import java.math.BigDecimal;

public interface OrderSkuEvaluateService extends IService<OrderSkuEvaluate> {
    /**
     * 统计评论数量
     *
     * @param spuId      spu id
     * @param score      评分
     * @param hasPicture 是否有图
     * @return 评论数量
     */
    Integer countEvaluate(String spuId, BigDecimal score, Boolean hasPicture);

    /**
     * 通过标签统计评论数
     *
     * @param tag 标签
     * @return 评论数
     */
    Integer countEvaluateByTag(String tag);

    /**
     * 获取评价分页数据
     *
     * @param queryVo 查询条件
     * @return 评价分页数据
     */
    Page<OrderEvaluateVo> findByPage(EvaluateQueryVo queryVo);
}
