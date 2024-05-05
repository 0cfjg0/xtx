package com.itheima.xiaotuxian.vo.member.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddressVo {
    private String id;
    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiver;
    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    private String contact;
    /**
     * 是否为默认，0为是，1为否
     */
    private Integer isDefault;
    /**
     * 所在省份编码
     */
    @NotBlank(message = "省份不能为空")
    private String provinceCode;
    /**
     * 所在城市编码
     */
    @NotBlank(message = "城市不能为空")
    private String cityCode;
    /**
     * 所在区/县编码
     */
    @NotBlank(message = "区/县不能为空")
    private String countyCode;
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String address;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 地址标签,以英文逗号分割
     */
    private String addressTags;
}
