package com.itheima.xiaotuxian.entity.search;

import lombok.Data;

import java.util.List;

@Data
public class EsKeyword {
    /**
     * 关键词Id
     */
    private String id;
    /**
     * 关键词
     */
    private String title;
    /**
     * 联想词集合
     */
    private List<String> associatedWords;
}
