package com.itheima.xiaotuxian.vo.member.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginSocialVo {
    /**
     * unionId
     */
    @NotBlank(message = "unionId不能为空")
    private String unionId;
    /**
     * 注册来源 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios,6为qq,7为微信
     */
    private String source;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String code;
}
