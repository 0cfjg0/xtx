package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: itheima
 * @Date: 2023/7/17 3:04 下午
 * @Description:
 */
@Data
public class MaterialVideoVo {
    /**
     * id
     */
    private String id;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 所属视频组id
     */
    private MaterialVideoGroupVo group;
    /**
     * 视频时长，单位秒
     */
    private Long duration;
    /**
     * 文件大小k
     */
    private Long fileSize;
    /**
     * 视频链接
     */
    private String url;
    /**
     * 状态，1为正常，2为回收站
     */
    private Integer state;

    /**
     * 视频封面集合
     */
    private List<Map<String, Object>> covers;
    /**
     * 类型，1为pc，2为app
     */
    private Integer type;
}
