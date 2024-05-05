package com.itheima.xiaotuxian.vo.member.response;

import lombok.Data;

@Data
public class ResetResultVo {
    /**
     * 脱敏手机号
     */
    private String mobile;
    /**
     * 验证票据
     */
    private String ticket;
}
