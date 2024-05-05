package com.itheima.xiaotuxian.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderSkuMapper extends BaseMapper<OrderSku> {

    Integer countOrderBySpuId(String spuId);
}
