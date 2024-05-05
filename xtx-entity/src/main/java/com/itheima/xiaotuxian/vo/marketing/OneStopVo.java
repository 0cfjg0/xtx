package com.itheima.xiaotuxian.vo.marketing;

import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import lombok.Data;

import java.util.List;

@Data
public class OneStopVo {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 副标题
     */
    private String summary;
    /**
     * 专场图片
     */
    private String picture;
    /**
     * 商品集合
     */
    private List<GoodsItemResultVo> goods;
}
