package com.itheima.xiaotuxian.vo.home.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendVo {
    /**
     * 左侧图片
     */
    private String leftIcon;
    /**
     * 右侧图片
     */
    private String rightIcon;
    /**
     * 标题
     */
    private String title;
    /**
     * 简述
     */
    private String caption;
}
