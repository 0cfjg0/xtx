package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-品牌
 */
@Data
@TableName(value = "goods_brand")
public class GoodsBrand extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 品牌名
     */
    private String name;
    /**
     * 英文品牌名
     */
    private String nameEn;
    /**
     * 商标注册号
     */
    private String trademarkCode;
    /**
     * 品牌所有人
     */
    private String brandOwner;
    /**
     * 标语、宣传语
     */
    private String slogan;
    /**
     * 产地-国家
     */
    private String productionPlaceCountry;
    /**
     * 产地-省（或地区、或邦、或州）
     */
    private String productionPlaceState;
    /**
     * 备注
     */
    private String brandStory;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态，0为可用，1为不可用
     */
    private Integer state;
    /**
     * 编辑状态，0为草稿，1为发布
     */
    private Integer editState;
    /**
     * logo
     */
    private String logoId;
    /**
     * 品牌大图id
     */
    private String brandImageId;
    /**
     * 首字母
     */
    private String firstWord;
}
