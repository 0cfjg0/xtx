package com.itheima.xiaotuxian.vo.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderLogisticsVo {
    /**
     * 商品集合
     */
    private LogisticsVo company;
    private List<LogisticsDetailVo>  list;
    /***
     * 所选地址Id
     */
    private String picture;
    /**
     * 商品件数
     */
    private Long count;

}
