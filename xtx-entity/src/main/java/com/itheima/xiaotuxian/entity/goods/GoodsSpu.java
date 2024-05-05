package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-标准产品单位
 */
@Data
@TableName(value = "goods_spu")
public class GoodsSpu extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 后台分类id
     */
    private String classificationBackendId;
    /**
     * 品牌id
     */
    private String brandId;
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
     * 架类型，1为立即上架，2为定时上架，3为仓库，4为回收站，5为历史
     */
    private Integer shelfType;
    /**
     * 上架时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime shelfTime;
    /**
     * 商品状态
     */
    private Integer state;
    /**
     * 商品审核状态，1为待审核，2为审核通过，3为驳回
     */
    private Integer auditState;
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
     * 运费，保留2位小数
     */
    private BigDecimal transportCost;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
    /**
     * 累计销量
     */
    private Integer salesCount;
    /**
     * 售价
     */
    private BigDecimal price;
    /**
     * 库存
     */
    private Integer inventory;
}
