package com.itheima.xiaotuxian.vo.goods.keyword;

import lombok.Data;

import java.util.List;

@Data
public class KeywordSaveVo {
    /**
     * 关键词id
     */
    private String id;
    /**
     * 关键词
     */
    private List<String> title;
    /**
     * 联想词
     */
    private List<String> associateWords;
    /**
     * 状态，0为开启，1为关闭
     */
    private Integer state;
    /**
     * 关联关系集合
     */
    private List<KeywordRelationSimpleVo> relations;
}
