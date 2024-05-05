package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPropertyVo {
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GoodsPropertyVo> properties;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> propertyValues;
}
