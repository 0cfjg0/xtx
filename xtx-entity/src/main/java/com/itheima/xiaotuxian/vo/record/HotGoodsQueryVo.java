package com.itheima.xiaotuxian.vo.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotGoodsQueryVo {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 数量限制
     */
    private Integer limit;
    /**
     * 商品id
     */
    private String spuId;


    private Integer page = 1;

    private Integer pageSize = 10;
}
