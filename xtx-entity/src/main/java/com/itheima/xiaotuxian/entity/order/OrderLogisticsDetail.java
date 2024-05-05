package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "order_logistics_detail")
public class OrderLogisticsDetail extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 物流编号
     */
    private String orderLogisticsId;
    /**
     * 物流时间
     */
    private LocalDateTime logisticsTime;
    /**
     * 物流信息
     */
    private String logisticsInformation;

}
