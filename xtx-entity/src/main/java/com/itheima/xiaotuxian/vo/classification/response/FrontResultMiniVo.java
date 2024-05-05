package com.itheima.xiaotuxian.vo.classification.response;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;

@Data
public class FrontResultMiniVo {
    /**
     * 前台类目Id
     */
    private String id;
    /**
     * 前台类目名称
     */
    private String name;
    /**
     * 前台类目图片
     */
    private String icon;
}
