package com.itheima.xiaotuxian.entity.business;

import com.baomidou.mybatisplus.annotation.*;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/10 4:30 下午
 * @Description:
 */
@Data
@TableName("business_ad")
public class BusinessAd extends AbstractBasePO {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * pc尺寸头图 pc尺寸头图
     */
    private String bannerUrl;
//    /**
//     * app尺寸头图 app尺寸头图
//     */
//    private String bannerApp;
    /**
     * 投放渠道，1为全部，2为pc，3为app
     */
    private Integer distributionChannel;
    /**
     * 跳转链接
     */
    private String targetUrl;
    /**
     * 投放位置，1为首页，2为分类商品页
     */
    private Integer distributionSite;
    /**
     * 前台分类id
     */
    private String classificationFrontId;
    /**
     * 跳转链接的类型
     * 跳转类型1、页面2、H5 3、小程序（小程序使用）
     */
    private String targetType;
}
