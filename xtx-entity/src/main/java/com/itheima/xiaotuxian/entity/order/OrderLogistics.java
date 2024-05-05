package com.itheima.xiaotuxian.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "order_logistics")
public class OrderLogistics extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 地区编号
     */
    private String areaNo;
    /**
     * 收件人名称
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 物流商家
     */
    private String logisticsCode;
    /**
     * 物流商家名称
     */
    private String logisticsName;
    /**
     * 物流单号
     */
    private String logisticsNo;
}
