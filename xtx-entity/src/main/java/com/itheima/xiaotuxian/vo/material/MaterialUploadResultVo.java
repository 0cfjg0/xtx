package com.itheima.xiaotuxian.vo.material;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MaterialUploadResultVo {
    public MaterialUploadResultVo(String url, String coverImg, String squareCoverImg) {
        this.url = url;
        this.coverImg = coverImg;
        this.squareCoverImg = squareCoverImg;
    }

    /**
     * id
     */
    private String id;
    /**
     * 图片或视频链接
     */
    private String url;
    /**
     * 图片或视频文件大小
     */
    private Long fileSize;
    /**
     * 图片或视频名称
     */
    private String name;
    /**
     * 视频时长
     */
    private Long duration;
    /**
     * 图片或视频上传状态，1为上传成功，2为上传失败
     */
    private Integer state;
    /**
     * 上传结果
     */
    private String message;
    /**
     * 分组信息
     */
    private MaterialGroupSimpleVo group;
    /**
     * 封面图片，原尺寸
     */
    private String coverImg;
    /**
     * 封面图片，1：1
     */
    private String squareCoverImg;
}
