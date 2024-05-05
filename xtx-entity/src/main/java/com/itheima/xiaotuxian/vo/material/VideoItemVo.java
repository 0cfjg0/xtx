package com.itheima.xiaotuxian.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoItemVo {
    private String id;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 视频链接
     */
    private String url;
    /**
     * 视频时长，单位秒
     */
    private Long duration;
    /**
     * 文件大小，单位k
     */
    private Long fileSize;
    /**
     * 封面图片集合
     */
    private String picture;
    /**
     * 视频审核状态，1为待审核，2为审核通过（即正常），3为审核不通过，4为待人工审核
     */
    private Integer auditState;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 是否已使用
     */
    private Integer isUsed;
    /**
     * 视频尺寸
     */
    private String screenSize;
    /**
     * 对象类型，1为视频组，2为视频
     */
    private Integer itemType;
    /**
     * 所属组Id
     */
    private String groupId;
    /**
     * 所属分组名称
     */
    private String groupName;
    /**
     * 所属组信息
     */
    private MaterialGroupSimpleVo group;
}
