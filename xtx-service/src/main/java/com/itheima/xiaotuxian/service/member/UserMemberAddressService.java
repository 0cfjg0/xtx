package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMemberAddress;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;

import java.util.ArrayList;

public interface UserMemberAddressService extends IService<UserMemberAddress> {
    ArrayList<AddressSimpleVo> getAddressByUid(String uid);

    String getFullLocationAddress(String provinceCode, String cityCode, String countyCode);

    String getOtherFullLocationAddress(String provinceCode, String cityCode, String countyCode);
}
