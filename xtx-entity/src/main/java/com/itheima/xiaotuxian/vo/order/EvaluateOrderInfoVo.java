package com.itheima.xiaotuxian.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.xiaotuxian.vo.goods.goods.SkuSpecVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvaluateOrderInfoVo {
    /**
     * 规格信息集合
     */
    private List<SkuSpecVo> specs;
    /**
     * 购买数量
     */
    private Integer quantity;
    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
