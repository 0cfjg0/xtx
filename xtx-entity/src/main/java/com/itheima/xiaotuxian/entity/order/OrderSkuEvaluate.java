package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "order_order_sku_evaluate")
public class OrderSkuEvaluate extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 商品id
     */
    private String skuId;
    /**
     * spu id
     */
    private String spuId;
    /**
     * 评分，取值范围0-5
     */
    private BigDecimal score;
    /**
     * 印象标签
     */
    private String tags;
    /**
     * 评价正文
     */
    private String content;
    /**
     * 晒单照片，多图以英文逗号分割
     */
    private String pictures;
    /**
     * 是否匿名，0为否，1为是
     */
    private Boolean anonymous;
    /**
     * 点赞数
     */
    private Integer praiseCount;
    /**
     * 客服回复
     */
    private String officialReply;
}
