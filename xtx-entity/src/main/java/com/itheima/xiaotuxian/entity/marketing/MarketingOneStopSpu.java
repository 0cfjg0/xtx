package com.itheima.xiaotuxian.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "marketing_one_stop_spu")
public class MarketingOneStopSpu {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 专场id
     */
    private String oneStopId;
    /**
     * 商品id
     */
    private String spuId;
}
