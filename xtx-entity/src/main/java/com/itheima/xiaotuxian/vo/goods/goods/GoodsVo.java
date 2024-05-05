package com.itheima.xiaotuxian.vo.goods.goods;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.xiaotuxian.entity.manager.ManagerAdmin;

import lombok.Data;
/**
 * @author lbc
 * @date 2023年5月8日11:42:49
 */
@Data
public class GoodsVo {
    /**
     * id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品图片
     */
    private String picture;
    /**
     * 商品价格
     */
    // @JsonSerialize(using = PriceFormart.class)
    private String price;
    /**
     * 库存
     */
    private Integer inventory;
    /**
     * 累计销量
     */
    private Integer salesCount;
    /**
     * 商品状态，1为出售中，2为仓库中，3为已售罄，4为回收站，5为历史宝贝
     */
    private Integer state;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;
    /**
     * 提交人信息
     */
    private ManagerAdmin creator;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * 商品审核状态，1为待审核，2为审核通过，3为驳回
     */
    private Integer auditState;
    /**
     * 编辑状态，0为草稿，1为提交
     */
    private Integer editState;
    /**
     * 审核说明
     */
    private String auditDesc;
    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
}
