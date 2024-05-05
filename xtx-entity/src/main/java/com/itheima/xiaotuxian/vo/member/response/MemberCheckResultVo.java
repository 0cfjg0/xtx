package com.itheima.xiaotuxian.vo.member.response;
/*
 * @author: lbc
 * @Date: 2023-11-16 14:28:45
 * @Descripttion: 
 */

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class MemberCheckResultVo {
    private String id;
    /**
     * 用户名称
     */
    private String nickname;

    private String account;
    /**
     * 头像
     */
    private String avatar;
    // 2023年11月16日14:29:44 增加token字段
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    /**
     * 性别，男、女、未知
     */
    private String gender;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    /**
     * 城市编码
     */
//    private String cityCode;
    /**
     * 省份编码
     */
    private String fullLocation;
    /**
     * 职业
     */
    private String profession;
}
