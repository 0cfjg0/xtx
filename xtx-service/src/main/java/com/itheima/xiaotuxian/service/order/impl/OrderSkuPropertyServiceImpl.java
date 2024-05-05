package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;
import com.itheima.xiaotuxian.mapper.order.OrderSkuPropertyMapper;
import com.itheima.xiaotuxian.service.order.OrderSkuPropertyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class OrderSkuPropertyServiceImpl extends ServiceImpl<OrderSkuPropertyMapper, OrderSkuProperty> implements OrderSkuPropertyService {

    /**
     * 从订单的规格信息表中查询，并得到规格信息
     *
     * @param orderId
     * @param skuId
     * @param client
     * @param orderSkuProperties
     * @return
     */
    public String getOrderAttrsText(String orderId, String skuId, String client, ArrayList<OrderSkuProperty> orderSkuProperties){
        var sbAttrsText = new StringBuilder();
        this.list(Wrappers.<OrderSkuProperty>lambdaQuery()
                .eq(OrderSkuProperty::getOrderSkuId, skuId)
                .eq(OrderSkuProperty::getOrderId,orderId)).forEach(orderSkuProperty ->{
                Optional.ofNullable(orderSkuProperties).ifPresent(orderSkuProperties1->{
                    orderSkuProperties.add(orderSkuProperty);
                });
                if(CommonStatic.REQUEST_CLIENT_PC.equals(client)){
                    sbAttrsText.append(String.format("%s:%s ", orderSkuProperty.getPropertyMainName(), orderSkuProperty.getPropertyValueName()));
                }else{
                    sbAttrsText.append(String.format("%s， ", orderSkuProperty.getPropertyValueName()));
                }
        });
        if(sbAttrsText.lastIndexOf("， ") >0){
            sbAttrsText.delete(sbAttrsText.lastIndexOf("， "),sbAttrsText.length());
        }

        return sbAttrsText.toString();
    }
}
