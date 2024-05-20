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

    //购物车列表
    @GetMapping()
    public R getCartList() {
        System.out.println("-----------------------------------ok");
        List<CartVo> cartList = cartService.getCartList();
        return R.ok(cartList, "78945613");
    }

    /**
     * 获取购物车数量
     */

    @GetMapping("/count")
    public R getCartCount() {
        Integer count = cartService.getCartCount();
        return R.ok(count);
    }

    /**
     * 合并购物车
     *
     * @param cartSaveVoList
     * @return
     */
    @PostMapping("/merge")
    public R mergeCartCout(@RequestBody List<CartSaveVo> cartSaveVoList) {
        cartService.mergeCartCout(cartSaveVoList);
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
        cartSaveVo.setSkuId(skuId);
        CartVo cartVo = cartService.updateUserCart(cartSaveVo);
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
        System.out.println("---------------" + batchDeleteCartVo);
        cartService.deleteUserCart(batchDeleteCartVo);
        return R.ok();
    }

    /**
     * 保存
     *
     * @param cartSaveVo
     * @return
     */
    @PostMapping()
    public R<CartVo> saveCart(@RequestBody CartSaveVo cartSaveVo) {
        CartVo cartVo = cartService.saveCart(cartSaveVo);
        return R.ok(cartVo, R.SUCCESS);
    }
}
