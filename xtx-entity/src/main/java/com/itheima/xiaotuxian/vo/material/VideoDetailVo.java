package com.itheima.xiaotuxian.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoDetailVo {
    private String id;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 视频时长，单位秒
     */
    private Long duration;
    /**
     * 视频链接
     */
    private String url;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    /**
     * 文件大小，单位k
     */
    private Long fileSize;
    /**
     * 视频尺寸
     */
    private String screenSize;
    /**
     * 视频审核状态，1为待审核，2为审核通过（即正常），3为审核不通过，4为待人工审核
     */
    private Integer auditState;
    /**
     * 封面图片，原尺寸
     */
    private String coverImg;
    /**
     * 封面图片，1：1
     */
    private String squareCoverImg;
    /**
     * 1为原尺寸图片，2为1：1图片
     */
    private Integer coverMark;
    /**
     * 分组信息
     */
    private MaterialGroupSimpleVo group;
}
