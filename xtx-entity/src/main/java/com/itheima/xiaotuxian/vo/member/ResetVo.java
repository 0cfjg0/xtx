package com.itheima.xiaotuxian.vo.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ResetVo {
    /**
     * 验证票据
     */
    @NotBlank(message = "验证票据不能为空")
    private String ticket;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码格式错误")
    private String password;
}
