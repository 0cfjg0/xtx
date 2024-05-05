package com.itheima.xiaotuxian.service.marketing;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.marketing.MarketingTopic;

public interface MarketingTopicService extends IService<MarketingTopic> {
    /**
     * 获取专题分页数据
     *
     * @param page             页码
     * @param pageSize         页尺寸
     * @param classificationId 专题分类id
     * @param sortField        排序字段
     * @param sortRule         排序规则
     * @return 专题分页数据
     */
    Page<MarketingTopic> findByPage(Integer page, Integer pageSize, String classificationId, String sortField, String sortRule);
}
