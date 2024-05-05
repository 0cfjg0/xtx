package com.itheima.xiaotuxian.service.record;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.record.RecordOrderSpu;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsVo;

import java.util.List;

public interface RecordOrderSpuService extends IService<RecordOrderSpu> {
    /**
     * 获取热门商品
     *
     * @param queryVo 查询条件
     * @return 热门商品
     */
    List<HotGoodsVo> getHotGoods(HotGoodsQueryVo queryVo);

    /**
     * 根据用户表示查询其已购商品spuId列表
     * @param memberId
     * @return 商品spuId列表
     */
    List<String> getSpuIdByMemberId(String memberId);
}
