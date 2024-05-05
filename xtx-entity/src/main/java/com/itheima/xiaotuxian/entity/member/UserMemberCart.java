package com.itheima.xiaotuxian.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "user_member_cart")
public class UserMemberCart {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * sku id
     */
    private String skuId;
    /**
     * spu id
     */
    private String spuId;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 当前价格
     */
    private BigDecimal price;
    /**
     * 是否选中
     */
    private Boolean seleted;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
