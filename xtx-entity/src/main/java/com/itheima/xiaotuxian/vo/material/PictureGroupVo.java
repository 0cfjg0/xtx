package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/17 9:30 上午
 * @Description:
 */
@Data
public class PictureGroupVo {
    /**
     * 图片组id
     */
    private String id;
    /**
     * 图片组名称
     */
    private String name;
    /**
     * 父图片组信息
     */
    private PictureGroupVo parent;
    /**
     * 子图片组集合
     */
    private List<PictureGroupVo> children;
}
