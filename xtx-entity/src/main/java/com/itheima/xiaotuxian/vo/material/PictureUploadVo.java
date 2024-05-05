package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

@Data
public class PictureUploadVo {
    /**
     * id
     */
    private String id;
    /**
     * 所属图片组id，未提供此参数则统一归属为未分组
     */
    private String groupId;
    /**
     * 图片宽度样式 -1为手机图片 -2为800px -3为640px 正数为自定义宽度
     */
    private Integer styleWidth;
    /**
     * 是否添加水印，取值范围：true或false
     */
    private Boolean isMask;
}
