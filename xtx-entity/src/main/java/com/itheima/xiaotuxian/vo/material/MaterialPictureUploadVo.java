package com.itheima.xiaotuxian.vo.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: itheima
 * @Date: 2023/7/17 3:04 下午
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialPictureUploadVo {
    /**
     * 是否添加水印
     */
    private Boolean isMask = false;
    /**
     * 图片宽度样式 -1为手机图片 -2为800px -3为640px 正数为自定义宽度
     */
    private Integer styleWidth = 0;
    /**
     * 水印类型，1为文字，2为图片
     */
    private Integer maskType;
    /**
     * 水印文字
     */
    private String maskText;
    /**
     * 水印文字字体
     */
    private String maskTextType;
    /**
     * 水印文字字号
     */
    private Integer maskTextSize;
    /**
     * 水印文字rgb值
     */
    private String maskTextColor="000000";
    /**
     * 水印透明度 取值范围 0-100
     */
    private Integer maskShadow=0;
    /**
     * 水印基准点：nw：左上 north：中上 ne：右上 west：左中 center：中部 east：右中 sw：左下 south：中下 se：右下
     */
    private String maskSite="center";
    /**
     * 所属图片组id
     */
    private String groupId;
}
