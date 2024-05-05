package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-商品主图视频
 */
@Data
@TableName(value = "goods_spu_main_video")
public class GoodsSpuMainVideo {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * spuid
     */
    private String spuId;
    /**
     * 视频id
     */
    private String videoId;
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
