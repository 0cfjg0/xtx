package com.itheima.xiaotuxian.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "user_member_property")
public class UserMemberProperty extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 身高，单位cm
     */
    private BigDecimal height;
    /**
     * 体重,单位kg
     */
    private BigDecimal bodyWeight;
    /**
     * 肩宽，单位cm
     */
    private BigDecimal shoulderWidth;
    /**
     * 胸围，单位cm
     */
    private BigDecimal chestCircumference;
    /**
     * 腰围，单位cm
     */
    private BigDecimal waistCircumference;
    /**
     * 臀围，单位cm
     */
    private BigDecimal hips;
    /**
     * 脚长，单位cm
     */
    private BigDecimal footSize;
    /**
     * 脚围，单位cm
     */
    private BigDecimal footCircumference;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 性别，男、女、未知
     */
    private String gender;
    /**
     * 是否为默认,0为是，1为否
     */
    private Integer isDefault;
}
