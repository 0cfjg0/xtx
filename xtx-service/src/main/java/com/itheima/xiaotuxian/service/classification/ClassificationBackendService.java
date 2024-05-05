package com.itheima.xiaotuxian.service.classification;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackend;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendBrand;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendPropertyGroup;
import com.itheima.xiaotuxian.vo.classification.*;

import java.util.HashSet;
import java.util.List;

public interface ClassificationBackendService extends IService<ClassificationBackend> {
        /**
         * 保存后台类目
         *
         * @param backendVo 类目信息
         * @return 后台类目id
         */
        String saveBackend(BackendSaveVo backendVo, String opUser);

        /**
         * 批量保存后台类目与属性组关联
         *
         * @param backendId             后台类目id，用于清除原有关系
         * @param backendPropertyGroups 关联信息集合
         * @return 操作结果
         */
        Boolean saveBackendPropertyGroupBatch(String backendId,
                        List<ClassificationBackendPropertyGroup> backendPropertyGroups);

        /**
         * 批量保存后台类目与品牌关联
         *
         * @param backendId     后台类目id，用于清除原有关系
         * @param backendBrands 关联信息集合
         * @return 操作结果
         */
        Boolean saveBackendBrandBatch(String backendId, List<ClassificationBackendBrand> backendBrands);

        /**
         * 批量删除
         *
         * @param ids 后台类目集合
         * @return 操作结果
         */
        Boolean batchDelete(List<String> ids);

        /**
         * 删除
         *
         * @param id 后台类目id
         * @return 操作结果
         */
        Boolean delete(String id);

        /**
         * 后台类目启、禁用
         *
         * @param backend    后台类目Id
         * @param state 状态
         * @return 操作结果
         */
        Boolean saveState(ClassificationBackend backend, Integer state);

        /**
         * 根据条件获取后台分类列表
         *
         * @return 后台分类列表
         */
        List<ClassificationBackend> findAll(BackendQueryVo queryVo);

        /**
         * 获取后台分类分页数据
         *
         * @param page     页码
         * @param pageSize 页尺寸
         * @param pid      父分类Id
         * @return 后台分类分页数据
         */
        Page<ClassificationBackend> findByPage(Integer page, Integer pageSize, String pid);

        /**
         * 获取后台分类分页数据
         *
         * @param page     页码
         * @param pageSize 页尺寸
         * @param queryVo  多条件
         * @return 后台分类分页数据
         */
        Page<ClassificationBackend> findByPage(Integer page, Integer pageSize, BackendQueryVo queryVo);

        /**
         * 统计关联品牌数量
         *
         * @param id 后台类目Id
         * @return 数量
         */
        Integer countBrands(String id);

        /**
         * 获取后台类目详情
         *
         * @param id 后台类目Id
         * @return 后台类目详情
         */
        BackendDetailVo findDetailById(String id);

        /**
         * 根据条件获取后台类目与属性组关联列表
         *
         * @param backendId 后台类目id
         * @return 后台类目与属性组关联列表
         */
        List<ClassificationBackendPropertyGroup> findAllBackendPropertyGroup(String backendId, Integer propertyType,
                        String propertyGroupId);

        /**
         * 获取后台分类链
         *
         * @param backendId 后台分类id
         * @return 后台分类链
         */
        BackendSimpleVo findBackendLink(String backendId);

        /**
         *
         * @param backendId
         * @return
         */
        String getBackendLinkId(String backendId);

        /**
         *
         * @param backendId
         * @return
         */
        List<BackendSimpleVo> getBackendLinkList(String backendId);


        /**
         * 根据第三级的后台类目id,查询所有的后台类目信息
         * @param threeLayerBackendId 三级类目id
         * @return 返回数据[一级类目，二级类目，三级类目]
         */
        List<BackendByLayerListVo> getBackendListByLayer(String threeLayerBackendId);

        /**
         * 查询后台类目id下的所有子类目id信息
         * 
         * @param backendId
         * @return
         */
        List<String> getChilrenIds(String backendId);

        /**
         * 根据前台类目id找到关联的后台类目id及子类目的信息
         * 
         * @param frontId
         * @return
         */
        HashSet<String> findBackendIdsByFrontIdDown(String frontId);

        List<BackendSimpleVo> findBackendsByFrontIdUp(String frontId);

        /**
         * 转换后台类目的分页结果
         * 
         * @param records
         * @return
         */
        List<BackendPageListVo> getChildrenBackendByLayer1PageList(List<ClassificationBackend> records);

        /**
         * 新版的获取数据详情
         * @param id
         * @return
         */
        BackendDetailVo findDetailByIdNew(String id);
}