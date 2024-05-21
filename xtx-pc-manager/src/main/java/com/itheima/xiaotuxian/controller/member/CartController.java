package com.itheima.xiaotuxian.controller.member;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberCartService;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/member/cart")
public class CartController extends BaseController {
    @Autowired
    private UserMemberCartService cartService;

    /**
     * 获取购物车数量
     */

    @GetMapping("/count")
    public R getCartCount() {
        String userId = getUserId();
        Integer count = cartService.getCartCount(userId);
        return R.ok(count);
    }

    /**
     * 1. 保存购物车商品信息,返回需要的的cartVo
     */
    @PostMapping()
    public R<CartVo> saveCart(@RequestBody CartSaveVo cartSaveVo) {
        //调用BaseController中,getUserId方法获取token携带的用户ID,实际调用getTokenValue,来获取id
        String userId = getUserId();
        CartVo cartVo = cartService.saveCart(cartSaveVo, userId);
        return R.ok(cartVo, R.SUCCESS);
    }

    /**
     * 合并购物车
     *
     * @param cartSaveVoList
     * @return
     */
    @PostMapping("/merge")
    public R mergeCartCout(@RequestBody List<CartSaveVo> cartSaveVoList) {
        System.out.println("------123-------------" + cartSaveVoList);
        String userId = getUserId();
        System.out.println("-----------------------456-" + userId);
        cartService.mergeCartCout(cartSaveVoList, userId);
        return R.ok();
    }

    /**
     * 2. 获取用户购物车列表
     */
    @GetMapping("")
    public R getCarts() {
        String userId = getUserId();
        List<CartVo> list = cartService.getCarts(userId);
        return R.ok(list, R.SUCCESS);
    }

    /**
     * 3. 购物车全选/全不选
     *
     * @return
     */
    @PutMapping("/selected")
    public R selectAllCarts(@RequestBody CartSelectedVo cartSelectedVo) {
        cartService.selectAllCarts(cartSelectedVo);
        return R.ok();
    }

    /**
     * 修改数据库
     *
     * @param cartSaveVo
     * @param skuId
     * @return
     */
    @PutMapping("/{skuId}")
    public R updateUserCart(@RequestBody CartSaveVo cartSaveVo, @PathVariable String skuId) {
        String userId = getUserId();
        cartSaveVo.setSkuId(skuId);
        CartVo cartVo = cartService.updateUserCart(cartSaveVo, userId);
        return R.ok(cartVo, "修改成功");
    }

    /**
     * 清空/删除购物车商品
     *
     * @param batchDeleteCartVo
     * @return
     */
    @DeleteMapping()
    public R deleteUserCart(@RequestBody BatchDeleteCartVo batchDeleteCartVo) {
//        System.out.println("---------------" + batchDeleteCartVo);
        String userId = getUserId();
        cartService.deleteUserCart(batchDeleteCartVo, userId);
        return R.ok();
    }


}
