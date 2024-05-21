package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.entity.goods.GoodsBrand;
import com.itheima.xiaotuxian.vo.goods.brand.*;

import java.util.HashSet;
import java.util.List;

public interface GoodsBrandService extends IService<GoodsBrand> {
    /**
     * 保存品牌信息
     *
     * @param saveVo 品牌信息
     * @return 操作结果
     */
    Boolean saveBrand(BrandSaveVo saveVo, String opUser);

    /**
     * 删除品牌
     *
     * @param id 品牌Id
     * @return 操作结果
     */
    Boolean deleteBrand(String id);

    /**
     * 批量删除品牌
     *
     * @param ids 品牌Id集合
     * @return 操作结果
     */
    Boolean batchDeleteBrand(List<String> ids);

    /**
     * 获取品牌分页数据
     *
     * @param queryVo 查询条件
     * @return 品牌分页数据
     */
    Page<BrandVo> findByPage(BrandQueryVo queryVo);

    /**
     * 获取品牌详情
     *
     * @param id 品牌id
     * @return 品牌详情
     */
    BrandDetailVo findDetailById(String id);



    List<BrandSimpleVo> findAll(BrandQueryVo queryVo);



    /**
     * 根据条件获取品牌实体列表
     *
     * @param queryVo 查询条件
     * @return 品牌实体列表
     */
    List<GoodsBrand> findAllEntities(BrandQueryVo queryVo);

    /**
     * 获取全部产地-国家维度
     *
     * @return 全部产地
     */
    List<String> findAllProductionPlace();

    /**
     * 获取全部首字母
     *
     * @return
     */
    List<String> findAllFirstWord();

    /**
     * 获取全部前台类目
     *
     * @return 全部前台类目
     */
    List<ClassificationFront> findAllFront();

    /**
     * 根据后台类目id信息查找品牌信息
     * @param frontBackEndIds
     * @return
     */
    List<BrandSimpleVo> findBrandsByBackendIds(HashSet<String> frontBackEndIds);

    List<BrandSimpleVo> getHotBrand();
}
