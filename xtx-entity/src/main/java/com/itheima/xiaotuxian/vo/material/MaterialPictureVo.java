package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/18 4:02 下午
 * @Description:
 */
@Data
public class MaterialPictureVo {
    private String id;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片宽度样式 -1为手机图片 -2为800px -3为640px 正数为自定义宽度
     */
    private Integer styleWidth;
    /**
     * 所属文件夹
     */
    private PictureGroupVo group;
    /**
     * 图片链接
     */
    private String url;
    /**
     * 文件大小，单位k
     */
    private Long fileSize;
    /**
     * 状态，1为正常，2为回收站
     */
    private Integer state;
    /**
     * 图片审核状态，1为待审核，2为审核通过（即正常），3为审核不通过，4为待人工审核
     */
    private Integer auditState;
    /**
     * 上传时间
     */
    private String updateTime;
    /**
     * 是否被使用
     */
    private Boolean isUsed;
    /**
     * 类型，1为pc，2为app
     */
    private Integer type;
}
