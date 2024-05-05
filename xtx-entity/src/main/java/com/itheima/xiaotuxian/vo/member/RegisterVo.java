package com.itheima.xiaotuxian.vo.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterVo {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(max = 20, message = "用户名格式错误")
    private String account;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号格式错误")
    private String mobile;
    /**
     * 手机验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码格式错误")
    private String code;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码格式错误")
    private String password;
    /**
     * unionId
     */
    private String unionId;

    /**
     * type(默认不传值，是pc注册；传app，是app传值)
     * TODO 小程序呢？
     */
    private String type;

}
