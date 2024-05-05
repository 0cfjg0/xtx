package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodsDetailVo {
    /**
     * id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * spu code
     */
    private String spuCode;
    /**
     * 所属分类信息
     */
    private BackendSimpleVo backend;
    /**
     * 品牌信息
     */
    private BrandSimpleVo brand;
    /**
     * 主图片信息
     */
    private GoodsPictureVo mainPictures;
    /**
     * 主视频集合
     */
    private GoodsVideoVo mainVideos;
    /**
     * 商品图片信息
     */
    private GoodsPictureVo pictures;
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
     * 运费，保留2位小数
     */
    private BigDecimal transportCost;
    /**
     * 上架类型，1为立即上架，2为定时上架，3为仓库
     */
    private Integer shelfType;
    /**
     * 上架时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shelfTime;
    /**
     * 审核信息集合
     */
    private List<AuditLogVo> auditLogs;
    /**
     * 非销售属性集合
     */
    private List<GoodsPropertyVo> spuProperties;
    /**
     * 商品sku信息
     */
    private SkuInfoVo skuInfo;
}
