package com.itheima.xiaotuxian.service.search;

import com.itheima.xiaotuxian.entity.search.EsKeyword;

import java.util.List;

public interface SearchKeywordService {
    /**
     * 保存关键词
     *
     * @param esKeyword 关键词信息
     * @return 操作结果
     */
    Boolean saveKeyword(EsKeyword esKeyword);

    /**
     * 删除关键词
     *
     * @param id 关键词Id
     * @return 操作结果
     */
    Boolean deleteKeyword(String id);

    /**
     * 通过关键词获取提示语
     *
     * @param keyword 关键词
     * @return 提示语集合
     */
    List<EsKeyword> findAll(String keyword);
}
