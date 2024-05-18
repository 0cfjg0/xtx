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
     * @param cartSaveVo
     * @return
     */
    @PostMapping("/merge")
    public R mergeCartCout(@RequestBody CartSaveVo cartSaveVo) {
        cartService.mergeCartCout(cartSaveVo);
        return R.ok();
    }
}
