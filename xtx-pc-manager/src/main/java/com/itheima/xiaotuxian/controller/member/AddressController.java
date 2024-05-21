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
    @PostMapping("")
    public R inAddress(@RequestBody AddressVo addressVo) {
        String userId = "1663375385531781122";
//        String userId = getUserId();
        addressService.inAddress(addressVo, userId);
        return R.ok(0, "坤坤想换家");
    }


    /**
     * 更新收货地址
     *
     * @param addressVo 收货地址信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public R updateAddress(@RequestBody AddressVo addressVo, @PathVariable String id) {
        addressVo.setId(id);
        addressService.updateAddress(addressVo);
        return R.ok(id, "福建烤老鼠大");
    }

    /**
     * 删除收货地址
     *
     * @param id 收货地址id
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public R delectAddress(@PathVariable String id) {
        addressService.delAddress(id);
        return R.ok(id, "舒服撒活动");
    }

    /**
     * 查询收货地址详情
     *
     * @param id 收货地址id
     * @return 操作结果
     */
    @GetMapping("/{id}")
    public R getAddress(@PathVariable String id) {
        AddressSimpleVo addressSimpleVo = addressService.getAddress(id);
        return R.ok(addressSimpleVo, "节日肺癌");
    }

    /**
     * 获取收货地址列表
     *
     * @return 收货地址列表
     */
    @GetMapping("")
    public R getAddresses() {
        String userId = "1663375385531781122";
//        String userId = getUserId();
        List<AddressSimpleVo> list = addressService.getAddressByUid(userId);
        return R.ok(list, "附件扣篮大赛");
    }


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
