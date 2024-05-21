package com.itheima.xiaotuxian.vo.order;

import com.itheima.xiaotuxian.vo.member.CartVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    /***
     * 生成订单Id
     */
    private String id;

    private String payType;

    private String payChannel;
}
