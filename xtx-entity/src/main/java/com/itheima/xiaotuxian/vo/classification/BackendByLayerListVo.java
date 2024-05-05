package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

/**
 * @author: lvbencai
 * @Date: 2022年4月3日17:54:58
 * @Description:
 */
@Data
public class BackendByLayerListVo {
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 层级，从1开始
     */
    private Integer layer;

}
