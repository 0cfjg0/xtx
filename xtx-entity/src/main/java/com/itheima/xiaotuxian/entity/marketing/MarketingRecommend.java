package com.itheima.xiaotuxian.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "marketing_recommend")
public class MarketingRecommend extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 推荐类型 推荐类型，1为特惠推荐
     */
    private Integer recommendType;
    /**
     * 商品id
     */
    private String spuId;
    /**
     * 折扣，取值范围0-10，保留两位小数
     */
    private BigDecimal discount;
}
