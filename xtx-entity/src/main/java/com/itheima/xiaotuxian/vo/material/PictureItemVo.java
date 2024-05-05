package com.itheima.xiaotuxian.vo.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PictureItemVo {
    private String id;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片宽度，单位px
     */
    private Integer width;
    /**
     * 图片高度，单位px
     */
    private Integer height;
    /**
     * 图片链接
     */
    private String url;
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
     * 文件大小，单位k
     */
    private Long fileSize;
    /**
     * 图片审核状态，1为待审核，2为审核通过（即正常），3为审核不通过，4为待人工审核
     */
    private Integer auditState;
    /**
     * 展示渠道，1为手机，2为PC
     */
    private Integer display;
    /**
     * 对象类型，1为图片组，2为图片
     */
    private Integer itemType;
    /**
     * 所属组Id
     */
    private String groupId;
    /**
     * 所属组名
     */
    private String groupName;
    /**
     *
     */
    private String picture;
    /**
     * 所属组信息
     */
    private MaterialGroupSimpleVo group;
}
