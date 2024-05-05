package com.itheima.xiaotuxian.vo.member.request;

import lombok.Data;

/*
 * @author: lbc
 * @Date: 2023-07-09 14:59:13
 * @Descripttion: 
 */
@Data
public class LoginWxMinRequestVo {
   /**
     * code
     */
    private String code;
    /**
     * 手机号加密信息
     */
    private String encryptedData;
    /**
     * 手机加密相关信息
     */
    private String iv;

    private String rawData;
    private String signature;
    /**
     * 个人的，没有加密信息，传明文的手机号
     * 
     */
    private String phoneNumber;
    


    
}

