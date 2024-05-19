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
    private UserMemberCartService userMemberCartService;


    /**
     * 1. 保存购物车商品信息,返回需要的的cartVo
     */
    @PostMapping()
    public R<CartVo>  saveCart(@RequestBody CartSaveVo cartSaveVo){
        CartVo cartVo = userMemberCartService.saveCart(cartSaveVo);
        return R.ok(cartVo, R.SUCCESS);
    }

    /**
     * 2. 获取用户购物车列表
     */
    @GetMapping()
    public R getCarts(){
        List<CartVo> list = userMemberCartService.getCarts();
        return R.ok(list,R.SUCCESS);
    }

    /**
     * 3. 购物车全选/全不选
     * @return
     */
    @PutMapping("/selected")
    public R selectAllCarts(@RequestBody CartSelectedVo cartSelectedVo){
        userMemberCartService.selectAllCarts(cartSelectedVo);
        return R.ok();
    }

}
