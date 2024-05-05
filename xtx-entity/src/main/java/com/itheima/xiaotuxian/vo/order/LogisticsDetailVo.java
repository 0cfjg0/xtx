package com.itheima.xiaotuxian.vo.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticsDetailVo {
    /**
     * 商品集合
     */
    private String id;
    /***
     * 所选地址Id
     */
    private String text;
    /**
     * 商品件数
     */
    private LocalDateTime time;

}
