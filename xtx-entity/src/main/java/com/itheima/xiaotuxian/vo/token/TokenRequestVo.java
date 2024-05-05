package com.itheima.xiaotuxian.vo.token;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author lbc
 * @date 2023年5月8日20:24:41
 * @since 2023年5月8日20:24:51
 */
@Data
public class TokenRequestVo {
    @NotNull(message = "id信息错误")
    private String id;
    // @NotNull(message = "token信息错误")
    // private String token;
    @NotNull(message = "account信息错误")
    private String account;

}
