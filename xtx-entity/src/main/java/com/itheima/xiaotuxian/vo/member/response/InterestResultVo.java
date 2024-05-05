package com.itheima.xiaotuxian.vo.member.response;

import lombok.Data;

@Data
public class InterestResultVo {
    /**
     * id
     */
    private String id;
    /**
     * 兴趣分类名称
     */
    private String name;
    /**
     * 兴趣分类图片链接
     */
    private String url;
    /**
     * 是否选中
     */
    private Boolean check;
}
