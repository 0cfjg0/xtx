
package com.itheima.xiaotuxian.vo.home.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/*
 * @author: lbc
 * @Date: 2023-05-14 18:15:34
 * @Descripttion:
 */
@Data
public class ActivitiesV8Vo {
    /**
     * id
     */
    private String id;
    /**
     * 活动页图片
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bannerPicture;
    /**
     * 跳转链接
     */
    // private String hrefUrl;
    /**
     * 活动名称
     */
    private String title;
    /**
     * 副标题
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String summary;

    /**
     * 子类项具体详情合集
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ActivitiesV8Vo> subTypes;

    /**
     * 存放分页数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Pager<GoodsItemResultVo> goodsItems;

}
