package com.itheima.xiaotuxian.vo.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class MobileLoginVo {
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "用户名格式错误")
    private String mobile;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码格式错误")
    private String code;
}
