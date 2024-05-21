package com.itheima.xiaotuxian.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.member.UserMemberAddress;
import org.apache.ibatis.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMemberAddressMapper extends BaseMapper<UserMemberAddress> {
    //新增地址
    @Insert("INSERT INTO user_member_address " +
            " (creator, id, member_id, receiver, contact, is_default, province_code, city_code, county_code, address, postal_code, address_tags, create_time, update_time )" +
            "  VALUES  ( #{creator},#{id}, #{memberId}, #{receiver}, #{contact}, #{isDefault}, #{provinceCode}, #{cityCode}, #{countyCode}, #{address},#{postalCode}, #{addressTags},#{createTime}, #{updateTime} )")
    void insertAddress(UserMemberAddress userMemberAddress);

    @Delete("delete from user_member_address where id = #{id}")
    void delectAddress(@Param("id") String id);
}
