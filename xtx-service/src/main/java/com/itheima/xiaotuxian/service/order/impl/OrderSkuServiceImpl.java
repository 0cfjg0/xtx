package com.itheima.xiaotuxian.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.order.Order;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.mapper.order.OrderSkuMapper;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderSkuServiceImpl extends ServiceImpl<OrderSkuMapper, OrderSku> implements OrderSkuService {

    @Autowired
    private OrderSkuMapper orderSkuMapper;

    @Autowired
    private GoodsService goodsService;


    @Override
    public Integer countOrderBySpuId(String spuId) {
        return orderSkuMapper.countOrderBySpuId(spuId);
    }

    @Override
    public void InsertSku(List<GoodsSku> listsku, Order order) {
        //insert对应商品
        for (GoodsSku goodsSku : listsku) {
            OrderSku orderSku = new OrderSku();
            orderSku.setOrderId(order.getId());
            orderSku.setSpuId(goodsSku.getSpuId());
            orderSku.setSkuId(goodsSku.getId());
            GoodsDetailVo goodsDetailVo = goodsService.findGoodsById(orderSku.getSpuId());
            orderSku.setImage(goodsDetailVo.getMainPictures().getPc().get(0).getUrl());
            System.out.println("url-----------"+orderSku.getImage());
            super.save(orderSku);
        }
    }


}
