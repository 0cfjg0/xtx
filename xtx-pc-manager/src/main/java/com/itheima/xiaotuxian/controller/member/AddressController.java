package com.itheima.xiaotuxian.controller.member;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMemberAddress;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.basic.BasicAreaInfoService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.request.AddressVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member/address")
public class AddressController extends BaseController {
    @Autowired
    private UserMemberAddressService addressService;
    @Autowired
    private BasicAreaInfoService basicAreaInfoService;
    /**
     * 添加收货地址
     *
     * @param addressVo 收货地址信息
     * @return 操作结果
     */










    /**
     * 更新收货地址
     *
     * @param addressVo 收货地址信息
     * @return 操作结果
     */









    /**
     * 删除收货地址
     *
     * @param id 收货地址id
     * @return 操作结果
     */







    /**
     * 查询收货地址详情
     *
     * @param id 收货地址id
     * @return 操作结果
     */







    /**
     * 获取收货地址列表
     *
     * @return 收货地址列表
     */






    /**
     * 检查操作是否有效
     *
     * @param addressId 地址id
     * @param userId    操作用户id
     */
    private UserMemberAddress checkAddress(String addressId, String userId) {
        var address = addressService.getById(addressId);
        if (address == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!address.getMemberId().equals(userId)) {
            throw new BusinessException(ErrorMessageEnum.ORDER_NO_PRIVILEGE);
        }
        return address;
    }

     /**
     * 删除收货地址
     *
     * @param id 收货地址id
     * @return 操作结果
     */


}
