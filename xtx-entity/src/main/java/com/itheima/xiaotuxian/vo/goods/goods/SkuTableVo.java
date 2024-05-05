package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuTableVo {
    /**
     * 列名
     */
    private String name;
    /**
     * 列数据集合
     */
    private List<String> values;
}
