package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class GoodsSpecItemVo {
    /**
     * 规格名称
     * property_main_name
     */
    private String name;

    /**
     * 规格名称id
     * property_main_id
     * 查询时，返回id信息，进行区分数据使用，不返回前端
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    /**
     * 可选值集合
     */
    private List<GoodsSpecValueVo> values;
}
