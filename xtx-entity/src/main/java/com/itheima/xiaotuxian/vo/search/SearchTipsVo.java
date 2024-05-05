package com.itheima.xiaotuxian.vo.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchTipsVo {
    /**
     * id
     */
    private List<String> ids;
    /**
     * 提示词或短语
     */
    private String associatedWord;
}
