package com.itheima.xiaotuxian.vo.home.response;

import lombok.Data;

/**
 * @author zsf
 */
@Data
public class BannerResultVo {
    /**
     * id
     */
    private String id;
    /**
     * banner链接
     */
    private String imgUrl;
    /**
     * 跳转链接
     */
    private String hrefUrl;
    /**
     * 跳转链接的类型
     * 跳转类型1、页面2、H5 3、小程序（小程序使用）
     */
    private String type;

}
