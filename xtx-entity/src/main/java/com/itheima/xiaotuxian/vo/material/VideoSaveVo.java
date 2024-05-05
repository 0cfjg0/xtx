package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

@Data
public class VideoSaveVo {
    /**
     * id
     */
    private String id;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 所属视频组id
     */
    private String groupId;
    /**
     * 视频时长，单位秒
     */
    private Long duration;
    /**
     * 文件大小k
     */
    private Long fileSize;
    /**
     * 1为原尺寸图片，2为1：1图片
     */
    private Integer coverMark;
    /**
     * 视频尺寸
     */
    private String screenSize;
    /**
     * 封面本地路径
     */
    private String localCoverPath;
    /**
     * 方形本地封面路径
     */
    private String localSquareCoverPath;
    /**
     * 封面图片，原尺寸
     */
    private String coverImg;
    /**
     * 封面图片，1：1
     */
    private String squareCoverImg;
    /**
     * 视频链接
     */
    private String url;
    /**
     * 操作人
     */
    private String opUser;
}
