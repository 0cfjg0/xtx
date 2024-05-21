package com.itheima.xiaotuxian.service.member.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsSku;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.goods.GoodsSkuMapper;
import com.itheima.xiaotuxian.mapper.goods.GoodsSpuMapper;
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

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;
    @Autowired
    private GoodsSpuMapper goodsSpuMapper;

    //下面是用户id和用户名,之后要改成从令牌获取
    private String memberId = "1609504249362780161";
    private String client = "a123456asd";


    /**
     * @param {UserMemberCart} cart
     * @param {String}         memberId
     * @param {String}         client
     * @return {*}
     * @description: 填充购物车的信息
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
            cartVo.setAttrsText(goodsService.getGoodsAttrsText(sku.getId(), client, specs));
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
     * 获取用户购物车列表
     * <p>
     * //     * @param memberId 用户Id
     *
     * @return 购物车列表
     */
    @Override
    @Transactional
    public List<CartVo> getCartList() {
//        System.out.println("---------------------------456");
        List<UserMemberCart> cartList = userMemberCartMapper.getCartList(memberId);
//        System.out.println(cartList + "========================");
        List<CartVo> cartVoList = new ArrayList<>();
        for (UserMemberCart userMemberCart : cartList) {
            cartVoList.add(fillCart(userMemberCart, memberId, client));
        }
        return cartVoList;
    }

    /**
     * 获取用户购物车数量
     *
     * @return
     */
    @Override
    public Integer getCartCount() {
        Integer count = userMemberCartMapper.sumQuntity(memberId);
        return count;
    }

    /**
     * 合并购物车
     *
     * @param cartSaveVoList
     */
    @Override
    public void mergeCartCout(List<CartSaveVo> cartSaveVoList) {
        List<UserMemberCart> cartList = userMemberCartMapper.getCartList(memberId);
        //判断重复添加的商品
        for (UserMemberCart userMemberCart : cartList) {
            String skuId = userMemberCart.getSkuId();
            for (int i = 0; i < cartSaveVoList.size(); i++) {
                if (skuId.equals(cartSaveVoList.get(i).getSkuId())) {
                    userMemberCart.setQuantity(userMemberCart.getQuantity() + cartSaveVoList.get(i).getCount());
                    userMemberCartMapper.updateById(userMemberCart);
                    cartSaveVoList.remove(i);
                    break;
                }
            }
        }
        for (CartSaveVo cartSaveVo : cartSaveVoList) {
            UserMemberCart userMemberCart = new UserMemberCart();
            //传下的数据
            userMemberCart.setSkuId(cartSaveVo.getSkuId());
            userMemberCart.setQuantity(cartSaveVo.getCount());
            userMemberCart.setSeleted(cartSaveVo.getSelected());
            //从数据库找到的数据
            //通过sku_id在goods_sku数据库表里查到squ_id
            GoodsSku goodsSku = goodsSkuMapper.selectById(cartSaveVo.getSkuId());
            System.out.println("goodsSku = " + goodsSku);
            userMemberCart.setSpuId(goodsSku.getSpuId());
            //通过squ_id在goods_squ数据库表里查到price
            GoodsSpu goodsSpu = goodsSpuMapper.selectById(goodsSku.getSpuId());
            userMemberCart.setPrice(goodsSpu.getPrice());

            userMemberCart.setMemberId(memberId);
            userMemberCart.setCreateTime(LocalDateTime.now());
            //添加进去
            userMemberCartMapper.insert(userMemberCart);
        }

    }

    /**
     * 修改购物车商品
     *
     * @param cartSaveVo
     * @return
     */
    @Override
    public CartVo updateUserCart(CartSaveVo cartSaveVo) {
        UserMemberCart cart = userMemberCartMapper.getCart(memberId, cartSaveVo.getSkuId());
        System.out.println("------------" + cartSaveVo);
        cart.setQuantity(cartSaveVo.getCount());
        cart.setSeleted(cartSaveVo.getSelected());
        int i = userMemberCartMapper.updateById(cart);
        System.out.println("==========================" + i);
        CartVo cartVo = fillCart(cart, memberId, client);
        return cartVo;
    }

    /**
     * 清空/删除购物车商品
     *
     * @param batchDeleteCartVo
     * @return
     */
    @Override
    public void deleteUserCart(BatchDeleteCartVo batchDeleteCartVo) {
        List<String> ids = batchDeleteCartVo.getIds();
        for (String id : ids) {
            userMemberCartMapper.deleteUserCart(memberId, id);
        }
    }

    /**
     * 新增购物车信息
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public CartVo saveCart(CartSaveVo cartSaveVo) {
        //1.创建 用户购物车 对象
        UserMemberCart userMemberCart = null;
        try {
            userMemberCart = userMemberCartMapper.getCart(memberId, cartSaveVo.getSkuId());
            System.out.println("-------------------" + userMemberCart);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (userMemberCart != null) {
            userMemberCart.setQuantity(userMemberCart.getQuantity() + cartSaveVo.getCount());
            updateById(userMemberCart);
        } else {
            //2.补全属性
            userMemberCart = new UserMemberCart();
            userMemberCart.setCreateTime(LocalDateTime.now());
            userMemberCart.setMemberId(memberId);

            userMemberCart.setSkuId(cartSaveVo.getSkuId());//商品ID
            userMemberCart.setQuantity(cartSaveVo.getCount());
            userMemberCart.setSeleted(cartSaveVo.getSelected());
            //从数据库找到的数据
            //通过sku_id在goods_sku数据库表里查到squ_id
            GoodsSku goodsSku = goodsSkuMapper.selectById(cartSaveVo.getSkuId());
            System.out.println("goodsSku = " + goodsSku);
            userMemberCart.setSpuId(goodsSku.getSpuId());
            //通过squ_id在goods_squ数据库表里查到price
            GoodsSpu goodsSpu = goodsSpuMapper.selectById(goodsSku.getSpuId());
            userMemberCart.setPrice(goodsSpu.getPrice());
            //3.保存 购物车信息 到 购物车表中
            userMemberCartMapper.insert(userMemberCart);
        }
        //4.调用上面的fillCart()方法,将cartVo对象响应到前端
        CartVo cartVo = this.fillCart(userMemberCart, memberId, client);
        System.out.println(cartVo);
        return cartVo;
    }
}
