package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

import java.util.List;

@Data
public class CartSelectedVo {
    /**
     * 是否选中
     */
    private Boolean selected;
    /**
     * 是否选中
     */
    private List<String> ids;
}
