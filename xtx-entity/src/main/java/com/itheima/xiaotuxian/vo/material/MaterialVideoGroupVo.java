package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/17 9:30 上午
 * @Description:
 */
@Data
public class MaterialVideoGroupVo {
    /**
     * 视频组id
     */
    private String id;
    /**
     * 视频组名称
     */
    private String name;
    /**
     * 父视频组信息
     */
    private MaterialVideoGroupVo parent;
    /**
     * 子视频组集合
     */
    private List<MaterialVideoGroupVo> children;
}
