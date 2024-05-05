package com.itheima.xiaotuxian.entity.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "record_order_spu")
public class RecordOrderSpu {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 订单Id
     */
    private String orderId;
    /**
     * 商品 spu id
     */
    private String spuId;
    /**
     * 下单时间
     */
    private LocalDateTime createTime;
}
