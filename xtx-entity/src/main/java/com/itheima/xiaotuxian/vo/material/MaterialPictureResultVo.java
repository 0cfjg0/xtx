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
public class MaterialPictureResultVo {
    /**
     * id
     */
    private String id;
    /**
     * 图片链接
     */
    private String url;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片大小
     */
    private Long fileSize;
    /**
     * 图片状态，1为上传成功，2为上传失败
     */
    private Integer state = 1;
    /**
     * 上传结果
     */
    private String message = "上传成功";
    /**
     * 所属文件夹
     */
    private PictureGroupVo group;
}
