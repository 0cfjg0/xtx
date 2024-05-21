package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;

import java.util.HashSet;
import java.util.List;

public interface GoodsSpuService extends IService<GoodsSpu> {
    /**
     * 根据后台类目id查询spu商品数量
     * 通过后台类目id统计商品数量
     * @param backendId
     * @return
     */
    Integer countByBackend(String backendId);

    /**
     * 根据查询的参数获取spu的分页信息
     * @param queryVo
     * @param pageResult
     * @param frontBackEndIds
     * @return
     */
    Page<GoodsSpu> getPageData(GoodsQueryPageVo queryVo, Page<GoodsSpu> pageResult, HashSet<String> frontBackEndIds);

    /**
     * 根据商品spuId列表查询对应的前台类目
     * @param spuIdList
     * @return
     */
    List<String> getFrontIdBySpuId(List<String> spuIdList);

    List<String> getFrontIdsByMemberId(String userId);


    List<GoodsItemResultVo> getNewGoods();
}
