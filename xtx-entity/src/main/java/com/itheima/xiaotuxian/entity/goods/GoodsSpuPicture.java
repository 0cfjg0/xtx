package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-商品图片
 */
@Data
@TableName(value = "goods_spu_picture")
public class GoodsSpuPicture {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * spuid
     */
    private String spuId;
    /**
     * 图片id
     */
    private String pictureId;
    /**
     * 类型 类型，1为pc，2为app
     */
    private Integer type;

    /**
     * 创建时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
}
