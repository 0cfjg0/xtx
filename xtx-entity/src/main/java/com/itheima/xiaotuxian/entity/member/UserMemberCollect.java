package com.itheima.xiaotuxian.entity.member;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "user_member_collect")
public class UserMemberCollect {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 收藏类型，1为商品，2为专题，3为品牌
     */
    private Integer collectType;
    /**
     * 收藏对象id
     */
    private String collectObjectId;

    /**
     * 创建时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
}
