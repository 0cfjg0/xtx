package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

@Data
public class PictureMaskVo {
    /**
     * 水印类型：text为文字水印，picture为图片水印
     */
    private String maskType;
    /**
     * 水印文字
     */
    private String maskText = "itcast";
    /**
     * 水印文字字体
     */
    private String maskTextType = "ZmFuZ3poZW5nc2h1c29uZw";
    /**
     * 水印文字rgb值
     */
    private String maskTextColor = "FFFFFF";
    /**
     * 图片水印透明度 取值范围 0-100
     */
    private Integer pictureMaskShadow = 100;
    /**
     * 图片水印基准点：nw：左上 north：中上 ne：右上 west：左中 center：中部 east：右中 sw：左下 south：中下 se：右下
     */
    private String pictureMaskSite = "nw";
    /**
     * 图片水印横轴偏移量
     */
    private Integer picturex = 0;
    /**
     * 图片水印纵轴偏移量
     */
    private Integer picturey = 0;

    /**
     * 文字水印透明度 取值范围 0-100
     */
    private Integer textMaskShadow = 100;
    /**
     * 文字水印基准点：nw：左上 north：中上 ne：右上 west：左中 center：中部 east：右中 sw：左下 south：中下 se：右下
     */
    private String textMaskSite = "nw";
    /**
     * 文字水印横轴偏移量
     */
    private Integer textx = 0;
    /**
     * 文字水印纵轴偏移量
     */
    private Integer texty = 0;
    /**
     * 水印图片链接
     */
    private String maskFileUrl;
    /**
     * 水印文字大小
     */
    private Integer maskTextSize = 16;
}
