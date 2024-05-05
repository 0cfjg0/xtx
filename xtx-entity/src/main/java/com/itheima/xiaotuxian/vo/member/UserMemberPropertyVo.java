package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserMemberPropertyVo {
    private String id;
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
