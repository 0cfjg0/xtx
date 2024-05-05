package com.itheima.xiaotuxian.vo.order;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:31
 * @Descripttion:
 */

import com.itheima.xiaotuxian.vo.member.AddressSimpleVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPreVo {
    /**
     * 地址信息集合
     */
    private List<AddressSimpleVo> userAddresses;
    /**
     * 商品集合
     */
    private List<OrderGoodsVo> goods;
    /**
     * 综述
     */
    private OrderPreSummaryVo summary;
}
