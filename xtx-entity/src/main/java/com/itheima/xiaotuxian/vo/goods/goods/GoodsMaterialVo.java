package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

@Data
public class GoodsMaterialVo {
    /**
     * 素材id
     */
    private String id;
    /**
     * 素材名称
     */
    private String name;
    /**
     * 素材链接
     */
    private String url;
    /**
     * 类型，1为pc，2为app
     */
    private Integer type;
    /**
     * 视频时长，单位秒
     */
    private Long duration;
    /**
     * 文件大小k
     */
    private Long fileSize;
    /**
     * 封面图片，原尺寸
     */
    private String coverImg;
    /**
     * 封面图片，1：1
     */
    private String squareCoverImg;
}
