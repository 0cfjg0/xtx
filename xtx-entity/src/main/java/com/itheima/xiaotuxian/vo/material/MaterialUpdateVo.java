package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import java.util.List;

@Data
public class MaterialUpdateVo {
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片文件，Base64
     */
    private String file;
    /**
     * 视频封面集合
     */
    private List<String> covers;
}
