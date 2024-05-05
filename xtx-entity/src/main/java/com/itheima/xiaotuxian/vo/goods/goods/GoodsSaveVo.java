package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.xiaotuxian.formart.PriceFormart;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsSaveVo {
    /**
     * id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 后台类目信息
     */
    private BackendSimpleVo backend;
    /**
     * 品牌信息
     */
    private BrandSimpleVo brand;
    /**
     * 库存减扣方式，1为拍下减库存，2为付款减库存
     */
    private Integer decreaseStockStyle;
    /**
     * 是否支持换货，0为支持，1为不支持
     */
    private Integer exchangeSupport;
    /**
     * 是否支持7天无理由退货，0为支持，1为不支持
     */
    private Integer noReasonToReturn;
    /**
     * 上架类型，1为立即上架，2为定时上架，3为仓库，4为回收站
     */
    private Integer shelfType;
    /**
     * 运费，保留2位小数
     */
    private BigDecimal transportCost;
    /**
     * 上架时间
     */
    private String shelfTime;
    /**
     * pc商品描述
     */
    private String pcDecription;
    /**
     * app商品描述
     */
    private String appDecription;
    /**
     * pc主图视频比例,1为1:1/16:9，2为3:4
     */
    private Integer pcVideoScale;
    /**
     * app主图视频比例,1为1:1/16:9，2为3:4
     */
    private Integer appVideoScale;
    /**
     * 编辑状态，0为草稿，1为提交
     */
    private Integer editState;
    /**
     * 商品主图集合
     */
    private GoodsPictureSaveVo mainPictures;
    /**
     * 主视频集合
     */
    private GoodsVideoSaveVo mainVideos;
    /**
     * 商品图片集合
     */
    private GoodsPictureSaveVo pictures;
    /**
     * 商品非销售属性集合
     */
    private List<GoodsPropertyValueSaveVo> spuProperties;
    /**
     * 商品销售属性集合
     */
    private List<GoodsSkuSaveVo> saleProperties;
    /**
     * spu编码
     */
    private String spuCode;


    /**
     * 一口价，不存在销售属性时，有此价格
     */
    @JsonSerialize(using = PriceFormart.class)
    private BigDecimal price;
}
