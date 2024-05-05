package com.itheima.xiaotuxian.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "user_member_favorite")
public class UserMemberFavorite {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String memberId;
    /**
     * 兴趣类型，1为前台分类
     */
    private Integer favoriteType;
    /**
     * 兴趣点id
     */
    private String favoriteObjectId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
