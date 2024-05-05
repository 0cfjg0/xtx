package com.itheima.xiaotuxian.vo.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartMultiVo {
    /**
     * 有效商品
     */
    private List<CartVo> valids;
    /**
     * 无效商品
     */
    private List<CartVo> invalids;
}
