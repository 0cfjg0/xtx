package com.itheima.xiaotuxian.vo.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberResultVo {
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
    /**
     * 性别，男、女、未知
     */
    private String gender;
    /**
     * 生日
     */
//    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    //@JsonSerialize(using = LocalDateTimeSerializer.class)
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
