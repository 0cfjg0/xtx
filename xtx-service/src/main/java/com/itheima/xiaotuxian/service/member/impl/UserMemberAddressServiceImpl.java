package com.itheima.xiaotuxian.service.member.impl;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:30
 * @Descripttion:
 */

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.basic.BasicAreaInfo;
import com.itheima.xiaotuxian.entity.member.UserMemberAddress;
import com.itheima.xiaotuxian.mapper.member.UserMemberAddressMapper;
import com.itheima.xiaotuxian.service.basic.BasicAreaInfoService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import com.itheima.xiaotuxian.vo.member.request.AddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserMemberAddressServiceImpl extends ServiceImpl<UserMemberAddressMapper, UserMemberAddress> implements UserMemberAddressService {
    @Autowired
    private BasicAreaInfoService basicAreaInfoService;
    @Autowired
    private UserMemberAddressMapper addressMapper;

    /**
     * 根据用户的id获取当前人的地址列表
     * 后期需要优化到缓存中
     *
     * @param uid
     * @return
     */
    @Override
    public ArrayList<AddressSimpleVo> getAddressByUid(String uid) {
        AtomicReference<ArrayList<AddressSimpleVo>> userAddresses = new AtomicReference<>();
        Optional.ofNullable(this.list(Wrappers.<UserMemberAddress>lambdaQuery().eq(UserMemberAddress::getMemberId, uid).orderByDesc(UserMemberAddress::getIsDefault, UserMemberAddress::getUpdateTime)))
                .ifPresent(addresses -> {
                    userAddresses.set(new ArrayList<AddressSimpleVo>());
                    addresses.forEach(address -> {
                        AddressSimpleVo addressSimpleVo = BeanUtil.toBean(address, AddressSimpleVo.class);
                        addressSimpleVo.setFullLocation(getFullLocationAddress(address.getProvinceCode(), address.getCityCode(), address.getCountyCode()));
                        userAddresses.get().add(addressSimpleVo);
                    });
                });
        return userAddresses.get();
    }

    /**
     * 根据address的省市区编号,获取对应的名称
     * 完整的行政区域地址如：江西省南昌市红谷滩区
     *
     * @param provinceCode
     * @param cityCode
     * @param countyCode
     * @return
     */
    @Override
    public String getFullLocationAddress(String provinceCode, String cityCode,
                                         String countyCode) {
        BasicAreaInfo province = basicAreaInfoService.getById(provinceCode);
        BasicAreaInfo city = basicAreaInfoService.getById(cityCode);
        BasicAreaInfo country = basicAreaInfoService.getById(countyCode);
        StringBuffer sbf = new StringBuffer();
        if (null != province) {
            sbf.append(province.getName()).append(" ");
            ;
        }
        if (null != city) {
            sbf.append(city.getName()).append(" ");
            ;
        }
        if (null != country) {
            sbf.append(country.getName());
        }
//        sbf.append(address);
        return sbf.toString();
    }

    @Override
    public String getOtherFullLocationAddress(String provinceCode, String cityCode, String countyCode) {
        BasicAreaInfo province = basicAreaInfoService.getById(provinceCode);
        BasicAreaInfo city = basicAreaInfoService.getById(cityCode);
        BasicAreaInfo country = basicAreaInfoService.getById(countyCode);
        StringBuffer sbf = new StringBuffer();
        if (null != province) {
            sbf.append(province.getName()).append(" ");
        }
        if (null != city) {
            sbf.append(city.getName()).append(" ");
        }
        if (null != country) {
            sbf.append(country.getName());
        }
//        sbf.append(address);
        return sbf.toString();
    }

    //addressVo补全变成AddressSimpleVo;
    UserMemberAddress allAddress(AddressVo addressVo, String userId) {
        UserMemberAddress userMemberAddress = new UserMemberAddress();
        userMemberAddress.setCreator(userId);
        userMemberAddress.setMemberId(userId);
        userMemberAddress.setReceiver(addressVo.getReceiver());
        userMemberAddress.setContact(addressVo.getContact());
        userMemberAddress.setIsDefault(addressVo.getIsDefault());
        userMemberAddress.setProvinceCode(addressVo.getProvinceCode());
        userMemberAddress.setCityCode(addressVo.getCityCode());
        userMemberAddress.setCountyCode(addressVo.getCountyCode());
        userMemberAddress.setAddress(addressVo.getAddress());
        userMemberAddress.setPostalCode(addressVo.getPostalCode());
        userMemberAddress.setAddressTags(addressVo.getAddressTags());
        userMemberAddress.setCreateTime(LocalDateTime.now());
        userMemberAddress.setUpdateTime(LocalDateTime.now());

        return userMemberAddress;
    }

    //UserMemberAddress转为AddressSimpleVo
    AddressSimpleVo allAddressSim(UserMemberAddress addressVo) {
        AddressSimpleVo userMemberAddress = new AddressSimpleVo();
        userMemberAddress.setId(addressVo.getId());
        userMemberAddress.setReceiver(addressVo.getReceiver());
        userMemberAddress.setContact(addressVo.getContact());
        userMemberAddress.setProvinceCode(addressVo.getProvinceCode());
        userMemberAddress.setCityCode(addressVo.getCityCode());
        userMemberAddress.setCountyCode(addressVo.getCountyCode());
        userMemberAddress.setAddress(addressVo.getAddress());
        userMemberAddress.setIsDefault(addressVo.getIsDefault());
        userMemberAddress.setFullLocation(getFullLocationAddress(addressVo.getProvinceCode(), addressVo.getCityCode(), addressVo.getCountyCode()));
        userMemberAddress.setPostalCode(addressVo.getPostalCode());
        userMemberAddress.setAddressTags(addressVo.getAddressTags());
        userMemberAddress.setSelected(false);
        return userMemberAddress;
    }

    //新增地址
    @Override
    public void inAddress(AddressVo addressVo, String userId) {
        UserMemberAddress userMemberAddress = allAddress(addressVo, userId);
        System.out.println("---" + userMemberAddress);
        addressMapper.insertAddress(userMemberAddress);
    }

    @Override
    public AddressSimpleVo getAddress(String id) {
        UserMemberAddress userMemberAddress = addressMapper.selectById(id);
        AddressSimpleVo addressSimpleVo = allAddressSim(userMemberAddress);
        return addressSimpleVo;
    }

    @Override
    public void updateAddress(AddressVo addressVo) {
        UserMemberAddress userMemberAddress = allAddress(addressVo, addressVo.getId());
        addressMapper.updateById(userMemberAddress);
    }

    @Override
    public void delAddress(String id) {
        addressMapper.delectAddress(id);
    }

}
