package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-最小存货单元
 */
@Data
@TableName(value = "goods_sku")
public class GoodsSku extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * spuid
     */
    private String spuId;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 可销售库存
     */
    private Integer saleableInventory;
    /**
     * 实际库存
     */
    private Integer physicalInventory;
    /**
     * 市场价
     */
    private BigDecimal marketPrice;
    /**
     * 售价
     */
    private BigDecimal sellingPrice;
    /**
     * 累计销量
     */
    private Integer salesCount;
}
