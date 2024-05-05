package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.goods.GoodsSkuPropertyValue;
import com.itheima.xiaotuxian.entity.goods.GoodsSpuMainPicture;
import com.itheima.xiaotuxian.vo.goods.goods.*;
import com.itheima.xiaotuxian.vo.goods.goods.goodsNew.GoodsDetailNewVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface GoodsService {
    /**
     * 保存商品信息
     *
     * @param saveVo 商品信息
     * @return 操作结果
     */
    Boolean saveGoods(GoodsSaveVo saveVo, String opUser);

    /**
     * 删除商品
     *
     * @param id 商品id
     * @return 操作结果
     */
    Boolean deleteById(String id);

    /**
     * 商品审核
     *
     * @param auditSaveVo 审核信息
     * @return 操作结果
     */
    Boolean auditGoods(GoodsAuditSaveVo auditSaveVo);

    /**
     * 修改可销售库存
     *
     * @param inventoryVo 修改信息
     * @return 操作结果
     */
    Boolean updateSaleableInventory(GoodsSaleableInventoryVo inventoryVo);

    /**
     * 商品上下架
     *
     * @param id    spu id
     * @param state 上下架
     * @return 操作结果
     */
    Boolean shelfGoods(String id, Integer state);

    /**
     * 获取spu信息
     *
     * @param id spuId
     * @return spu信息
     */
//    GoodsSpu getById(String id);

    /**
     * 获取商品分页数据
     *
     * @param queryVo 查询条件
     * @return 商品分页数据
     */
    Page<GoodsVo> findByPage(GoodsQueryPageVo queryVo);

    /**
     * 获取sku分页数据
     *
     * @param queryVo 查询条件
     * @return sku分页数据
     */
    Page<GoodsSkuVo> findSkuByPage(SkuQueryVo queryVo);

    /**
     * 获取商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    GoodsDetailVo findGoodsById(String id);

    /**
     * 新版本-获取商品详情信息
     * @param id
     * @return
     */
    GoodsDetailNewVo findGoodsByIdNew(String id);
    /**
     * 解除商品与后台分类绑定
     *
     * @param backendId 后台分类Id
     * @return 操作结果
     */
    Boolean undoBackend(String backendId);

    /**
     * 通过spu Id获取商品销售属性
     *
     * @param spuId spuID
     * @return 商品销售属性
     */
//    List<GoodsSkuPropertyValue> findAllSkuPropertyValueBySpu(String spuId);

    /**
     * 获取sku列表
     *
     * @param spuId spuId
     * @return sku列表
     */
    List<GoodsSku> findAllSku(String spuId);

    List<GoodsSpuMainPicture> findAllSpuMainPicture(String spuId, Integer type);

    String getSpuPicture(String spuId, Integer type);

    BigDecimal getSpuPrice(String spuId);

    /**
     * 统计回收站中数量
     *
     * @return 商品数量
     */
    Integer countByTrash();

    /**
     * 统计草稿箱中数量
     *
     * @return 商品数量
     */
    Integer countByEdit();

    /**
     * 通过id获取sku信息
     *
     * @param id skuid
     * @return sku信息
     */
    GoodsSku findSkuById(String id);

    /**
     * 获取销售商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    SaleGoodsDetailVo findSaleGoodsDetailsById(String id, String client, String userId);

    /**
     * 获取商品规格数据
     * @param spuId
     * @param client 商品sku id
     * @return 商品规格数据
     */
    GoodsSpecVo findGoodsSpecById(String spuId, String client);

    /**
     * 获取商品销售属性
     *
     * @param skuId skuId
     * @return 商品销售属性
     */
    List<GoodsSkuPropertyValue> findSkuPropertyValueBySkuId(String skuId);

    /**
     * 减库存
     *
     * @param skuId skuId
     * @param spuId spuId
     */
    void decreaseStock(String skuId, String spuId);

    /**
     * 将规格的处理封装到方法中
     * @param skuId
     * @param client
     * @param specs
     * @return
     */
    String getGoodsAttrsText(String skuId, String client, ArrayList<SkuSpecVo> specs);
}
