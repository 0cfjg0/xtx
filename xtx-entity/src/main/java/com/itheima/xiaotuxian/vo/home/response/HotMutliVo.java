package com.itheima.xiaotuxian.vo.home.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 移动端展示的数据-
 * @author lvbencai
 * @date 2023年5月5日15:33:45
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotMutliVo {
    /**
     * id
     */
    private String id;
    /**
     * 图片
     */
    private List<String> pictures;
    /**
     * 标题
     */
    private String title;
    /**
     * 说明
     */
    private String alt;

    /**
     * 目标链接地址
     */
    private String target;
    /**
     * 人气推荐类型1、特惠推荐 2、爆款推荐 3、一站买全4、领券中心
     */
    private Integer type;
}
