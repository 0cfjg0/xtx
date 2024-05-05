package com.itheima.xiaotuxian.vo.classification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageBannerVo {
    /**
     * 标题
     */
    private String title;
    /**
     * 图片链接
     */
    private String imgUrl;
    /**
     * 跳转链接
     */
    private String hrefUrl;
}
