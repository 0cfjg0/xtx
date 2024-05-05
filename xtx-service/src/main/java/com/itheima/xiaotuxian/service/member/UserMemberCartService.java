package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberCart;
import com.itheima.xiaotuxian.vo.member.BatchDeleteCartVo;
import com.itheima.xiaotuxian.vo.member.CartSaveVo;
import com.itheima.xiaotuxian.vo.member.CartVo;

import java.util.List;

public interface UserMemberCartService extends IService<UserMemberCart> {
    /**
     * 保存购物车商品信息
     *
     * @param saveVo 商品信息
     * @return 购物车商品信息
     */




    /**
     * 批量删除用户购物车商品
     *
     * @param batchDeleteCartVo    sku id集合
     * @param memberId 用户Id
     * @return 操作结果
     */





    /**
     * 获取用户购物车列表
     *
     * @param memberId 用户Id
     * @return 购物车列表
     */



}
