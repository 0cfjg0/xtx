package com.itheima.xiaotuxian.service.member.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.member.UserMemberCartMapper;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberCartService;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import com.itheima.xiaotuxian.vo.goods.goods.SkuSpecVo;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import com.itheima.xiaotuxian.vo.member.CartVo;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserMemberCartServiceImpl extends ServiceImpl<UserMemberCartMapper, UserMemberCart> implements UserMemberCartService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private MarketingRecommendService recommendService;
    @Autowired
    private UserMemberCollectService collectService;

    @Resource
    private UserMemberCartMapper userMemberCartMapper;


    
    /**
     * @description: 填充购物车的信息
     * @param {UserMemberCart} cart
     * @param {String} memberId
     * @param {String} client
     * @return {*}
     * @author: lbc
     */
    private CartVo fillCart(UserMemberCart cart, String memberId, String client) {
        var cartVo = new CartVo();
        cartVo.setId(cart.getSpuId());
        cartVo.setSkuId(cart.getSkuId());
        var goodsStateValid = new AtomicBoolean(false);
        Optional.ofNullable(goodsSpuService.getById(cartVo.getId())).ifPresent(spu -> {
            cartVo.setName(spu.getName());
            cartVo.setPicture(goodsService.getSpuPicture(spu.getId(), CommonStatic.REQUEST_CLIENT_PC.equals(client) ? CommonStatic.MATERIAL_SHOW_PC : CommonStatic.MATERIAL_SHOW_APP));
            goodsStateValid.set((GoodsStatic.STATE_STORING == spu.getState() || GoodsStatic.STATE_SELLING == spu.getState())
                    && GoodsStatic.AUDIT_STATE_PASS == spu.getAuditState()
                    && GoodsStatic.EDIT_STATE_SUBMIT == spu.getEditState());
            cartVo.setPostFee(spu.getTransportCost());
        });
        Optional.ofNullable(goodsService.findSkuById(cartVo.getSkuId())).ifPresent(sku -> {
            cartVo.setStock(sku.getSaleableInventory());
            //手机版的因为自己更改样式（只是值得拼接）,并且返回规格的集合
            var specs = new ArrayList<SkuSpecVo>();
            cartVo.setAttrsText(goodsService.getGoodsAttrsText(sku.getId(),client,specs));
            cartVo.setSpecs(specs);
            //当前价格
            cartVo.setNowOriginalPrice(sku.getSellingPrice());
            cartVo.setNowPrice(sku.getSellingPrice());
            Optional.ofNullable(recommendService.findOne(sku.getSpuId(), 1)).ifPresent(recommend -> {
                cartVo.setNowPrice((cartVo.getNowPrice().multiply(recommend.getDiscount())));
                cartVo.setNowPrice(cartVo.getNowPrice().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP));
            });
        });
        cartVo.setIsEffective(goodsStateValid.get() && cartVo.getStock() > 0);
        //处理折扣
        Optional.ofNullable(recommendService.findOne(cart.getSpuId(), 1)).ifPresent(recommend -> cartVo.setDiscount(recommend.getDiscount()));
        //处理收藏
        cartVo.setIsCollect(false);
        Optional.ofNullable(memberId).filter(StrUtil::isNotEmpty).ifPresent(uid ->
                Optional.ofNullable(collectService.countCollect(uid, cart.getSpuId(), UserMemberStatic.COLLECT_TYPE_GOODS)).ifPresent(count -> cartVo.setIsCollect(count > 0)));
        //处理其他信息
        cartVo.setPrice(cart.getPrice());
        cartVo.setSelected(cart.getSeleted());
        cartVo.setCount(cart.getQuantity());
        return cartVo;
    }

    /**
     * 新增购物车信息
     *
     * @param saveVo
     * @return
     */

}
