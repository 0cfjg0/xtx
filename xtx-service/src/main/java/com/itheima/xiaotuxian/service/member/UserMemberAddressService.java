package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberAddress;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.request.AddressVo;

import java.util.ArrayList;
import java.util.List;

public interface UserMemberAddressService extends IService<UserMemberAddress> {
    //    //地址列表
    ArrayList<AddressSimpleVo> getAddressByUid(String uid);

    String getFullLocationAddress(String provinceCode, String cityCode, String countyCode);

    String getOtherFullLocationAddress(String provinceCode, String cityCode, String countyCode);

    //添加地址
    void inAddress(AddressVo addressVo, String userId);

    //详细地址
    AddressSimpleVo getAddress(String id);

    //更新地址
    void updateAddress(AddressVo addressVo);

    //删除
    void delAddress(String id);

//    //地址列表
//    List<AddressSimpleVo> getAddresses(String userId);
}
