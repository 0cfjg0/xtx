package com.itheima.xiaotuxian.vo.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class AddressSimpleVo {
    private String id;
    /**
     * 收货人姓名
     */
    private String receiver;
    /**
     * 联系方式
     */
    private String contact;
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
     * 详细地址
     * 如： 解放路108号物质大厦1003室
     */
    private String address;
    /**
     * 是否为默认，0为是，1为否
     */
    private Integer isDefault;
    /**
     * 完整的行政区域地址如：江西省南昌市红谷滩区
     */
    private String fullLocation	;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 地址标签,以英文逗号分割
     */
    private String addressTags;
    /**
     * 是否是入参传值（是否已经选中此地址的标志）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean selected;
}
