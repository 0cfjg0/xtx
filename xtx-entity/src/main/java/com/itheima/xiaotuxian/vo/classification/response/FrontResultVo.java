package com.itheima.xiaotuxian.vo.classification.response;

import java.io.Serializable;
import java.util.List;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;

import lombok.Data;

@Data
public class FrontResultVo implements Serializable {
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
    private String picture;
    /**
     * 子类目集合
     */
    private List<FrontResultVo> children;
    /**
     * 推荐商品集合
     */
    private List<GoodsItemResultVo> goods;
}
