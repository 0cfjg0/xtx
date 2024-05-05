package com.itheima.xiaotuxian.entity.home;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 首页人气推荐信息
 * </p>
 *
 * @author lvbencai
 * @since 2023-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("home_hot_recommend")
public class HomeHotRecommend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

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
    private String targetUrl;

    /**
     * 发布渠道1、pc 2、app 3、小程序
     */
    private Integer distributionChannel;

    /**
     * 人气推荐类型1、特惠推荐 2、爆款推荐 3、一站买全4、领券中心
     */
    private Integer type;

    /**
     * 是否删除 是否删除，0为否1为是
     */
    private Integer isDelete;

    /**
     * 创建人 创建人id
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 左图片
     */
    private String pictureLeft;

    /**
     * 右图片
     */
    private String pictureRight;


}
