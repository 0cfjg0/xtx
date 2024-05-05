package com.itheima.xiaotuxian.vo.member;

import lombok.Data;

/**
 * @author: itheima
 * @Date: 2020/11/3 10:20 上午
 * @Description:
 */
@Data
public class OrderSkuPropertyVo {
    /**
     * 属性名称，如 颜色
     */
    private String propertyMainName;
    /**
     * 属性值名称，如 黑色
     */
    private String propertyValueName;
}
