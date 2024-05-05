package com.itheima.xiaotuxian.entity.material;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;


/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 素材-视频
 */
@Data
@TableName(value = "material_video")
public class MaterialVideo extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
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
     * 视频尺寸
     */
    private String screenSize;
    /**
     * 状态，1为正常，2为回收站
     */
    private Integer state;
    /**
     * 审核状态，1为待审核，2为审核中，3为审核通过，4为审核不通过，5为待人工审核
     */
    private Integer auditState;
    /**
     * 1为原尺寸图片，2为1：1图片
     */
    private Integer coverMark;
}
