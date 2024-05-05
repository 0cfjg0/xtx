package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsKeyword;
import com.itheima.xiaotuxian.vo.goods.keyword.KeywordPageQueryVo;
import com.itheima.xiaotuxian.vo.goods.keyword.KeywordQueryVo;
import com.itheima.xiaotuxian.vo.goods.keyword.KeywordSaveVo;
import com.itheima.xiaotuxian.vo.goods.keyword.KeywordVo;

import java.util.List;

public interface GoodsKeywordService extends IService<GoodsKeyword> {
    /**
     * 保存关键词信息
     *
     * @param saveVo 关键词信息
     * @param opUser 操作人
     * @return 操作结果
     */
    Boolean save(KeywordSaveVo saveVo, String opUser);

    /**
     * 批量删除关键字
     *
     * @param ids 关键字Id集合
     * @return 操作结果
     */
    Boolean batchDelete(List<String> ids);

    /**
     * 获取关键字分页数据
     *
     * @param queryVo 查询条件
     * @return 分页数据
     */
    Page<KeywordVo> findByPage(KeywordPageQueryVo queryVo);

    /**
     * 获取关键词详情
     *
     * @param id 关键字id
     * @return 关键字详情
     */
    KeywordVo findDetailById(String id);

    /**
     * 获取符合条件的关键字集合（并列）
     *
     * @param queryVo 查询条件
     * @return 关键字集合
     */
    List<GoodsKeyword> findAllByOr(KeywordQueryVo queryVo);
}
