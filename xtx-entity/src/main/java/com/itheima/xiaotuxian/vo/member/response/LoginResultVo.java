package com.itheima.xiaotuxian.vo.member.response;

import lombok.Data;

@Data
public class LoginResultVo {
    /**
     * id
     */
    private String id;
    /**
     * 用户名
     */
    private String account;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * token
     */
    private String token;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别，男、女、未知
     */
    private String gender;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 省份编码
     */
    private String provinceCode;
    /**
     * 职业
     */
    private String profession;
}
