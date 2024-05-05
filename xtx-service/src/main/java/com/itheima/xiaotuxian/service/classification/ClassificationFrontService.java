package com.itheima.xiaotuxian.service.classification;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.vo.classification.*;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultVo;

import java.util.List;

public interface ClassificationFrontService extends IService<ClassificationFront> {
    /**
     * 保存前台类目
     *
     * @param saveVo 前台类目信息
     * @return 操作结果
     */
    Boolean saveFront(FrontSaveVo saveVo, String opUser);

    Boolean saveFront(FrontSaveNewVo saveNewVo, String opUser);
    /**
     * 批量删除
     *
     * @param ids 待删除数据Id
     * @return 操作结果
     */
    Boolean batchDelete(List<String> ids);

    /**
     * 获取前台类目详情
     *
     * @param id 前台类目Id
     * @return 前台类目详情
     */
    FrontVo findDetailById(String id);

    /**
     * 获取本级和父级简要信息
     *
     * @param id 前台分类id
     * @return 前台分类及父级简要信息
     */
    FrontSimpleVo findSimpleById(String id);

    /**
     * 获取前台分类分页数据
     *
     * @param queryVo 查询条件
     * @return 分页数据
     */
    Page<FrontVo> findByPage(FrontPageQueryVo queryVo);

    /**
     * 获取有效分类
     *
     * @return 有效分类
     */
    List<ClassificationFront> findAllValidFront(String parentId);

    /**
     * 根据关联id和类型查询前台类目信息
     * @param relationId
     * @param type
     * @return
     */
    List<FrontSimpleVo> findFrontSimplesByRelationId(String relationId, int type);



    /**
     * @description: 获取首页的前台分类信息和分类下的物品
     * 物品个数根据pageSize决定
     * 物品的图片信息根据showClient决定
     * @param {int} pageSize
     * @return {*}
     * @author: lbc
     * @param pageSize
     * @param showClient
     */

    /**
     * @description: 查询首页的前台类目
     * @param {*}
     * @return {*}
     * @author: lbc
     */
    public List<FrontResultVo> findCategory() ;

    /**
     *
     * @param id
     * @return
     */
    FrontDetailNewVo findDetailNewById(String id);
}
