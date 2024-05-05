package com.itheima.xiaotuxian.vo.home.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotRecommendVo {
    /**
     * id
     */
    private String id;
    /**
     * 图片
     */
    private String picture;
    /**
     * 标题
     */
    private String title;
    /**
     * 说明
     */
    private String alt;
}
