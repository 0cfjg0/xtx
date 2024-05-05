package com.itheima.xiaotuxian.vo.home;

import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;

@Data
public class CategoryGoodsVo {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String picture;
    /**
     * 销售信息
     */
    private String saleInfo;
    /**
     * 子类集合
     */
    private List<FrontSimpleVo> children;
    /**
     * 商品集合
     */
    private List<GoodsItemResultVo> goods;
}
