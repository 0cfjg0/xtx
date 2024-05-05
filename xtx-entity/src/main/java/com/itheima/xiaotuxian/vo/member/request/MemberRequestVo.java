package com.itheima.xiaotuxian.vo.member.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRequestVo {
    private String id;
    /**
     * 用户名称
     */
    private String nickname;
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
     * 所在省份编码
     */
    private String provinceCode;
    /**
     * 所在城市编码
     */
    private String cityCode;
    /**
     * 所在区/县编码
     */
    private String countyCode;
    /**
     * 职业
     */
    private String profession;
}
