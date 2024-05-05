package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-关键字关联
 */
@Data
@TableName(value = "goods_keyword_relation")
public class GoodsKeywordRelation {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 关键字id
     */
    private String keywordId;
    /**
     * 关联标识
     */
    private String relationKey;
    /**
     * 关联对象id
     */
    private String objectId;
    /**
     * 关联数据类型，1为后台类目，2为销售属性组，3为品牌
     */
    private Integer objectType;
}
