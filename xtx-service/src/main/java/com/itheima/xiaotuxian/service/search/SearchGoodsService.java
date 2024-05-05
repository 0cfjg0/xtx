package com.itheima.xiaotuxian.service.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.search.SearchGoodsServiceVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;

import java.util.List;

/**
 * @author lvbencai
 * @date 2023年5月6日08:40:47
 */
public interface SearchGoodsService {
    /**
     * 保存商品至es
     *
     * @param id 商品id
     * @return 保存结果
     */
    Boolean saveGoods(String id);

    /**
     * 删除商品信息
     *
     * @param id 商品Id
     * @return 操作结果
     */
    Boolean deleteGoods(String id);


    /**
     * 通过前台分类关联统计商品数量
     *
     * @param relationKey 前台分类关联关系
     * @return 商品数量
     */
    Long countByFrontRelation(String relationKey);

    /**
     * 通过前台分类关联获取商品信息
     *
     * @param relationKey 前台分类关联关系
     * @param page        页码
     * @param pageSize    页尺寸
     * @return 商品信息集合
     */
    List<EsGoods> findAllByFrontRelation(String relationKey, Integer page, Integer pageSize);

    /**
     * 通过发布时间的顺序获取商品列表
     *
     * @param limit      最大条目数
     * @param sortMethod 排序方法：asc为正序、desc为倒序
     * @param isPre      是否为预售
     * @return 获取商品列表
     */
    List<EsGoods> findAllGoodsWithPublishTime(Integer page,Integer pageSize, String sortMethod, Boolean isPre);

    /**
     * 根据条件获取商品列表
     * @param queryVo 查询条件
     * @return 商品列表
     */
    List<EsGoods> findAllGoods(GoodsQueryPageVo queryVo);

    /**
     * 搜索商品数据
     *
     * @param queryVo 搜索条件
     * @return 商品数据
     */
    SearchGoodsServiceVo search(SearchQueryVo queryVo);

    /**
     * 分页搜索商品数据
     *
     * @param queryVo 搜索条件
     * @return 商品分页数据
     */
    Page<EsGoods> searchByPage(SearchQueryVo queryVo);

    /**
     * 更新商品订单数
     *
     * @param spuId 商品id
     * @param num   订单数
     */
    void updateGoodOrderNum(String spuId, Integer num);

    /**
     * 获取热销商品
     *
     * @param queryVo 查询数据
     * @return 热销商品
     */
    List<EsGoods> getHotGoods(HotGoodsQueryVo queryVo);
}
