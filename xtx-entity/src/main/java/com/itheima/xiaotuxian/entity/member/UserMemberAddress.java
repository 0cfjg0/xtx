package com.itheima.xiaotuxian.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "user_member_address")
public class UserMemberAddress extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 收货人姓名
     */
    private String receiver;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 是否为默认，0为是，1为否
     */
    private Integer isDefault;
    /**
     * 所在省份编码
     */
    private String provinceCode;
    /**
     * 所在城市编码
     */
    private String cityCode;
    /**
     * 所在区/县编码
     */
    private String countyCode;
    /**
     * 详细地址（街道楼号等信息）
     */
    private String address;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 地址标签,以英文逗号分割
     */
    private String addressTags;

}
