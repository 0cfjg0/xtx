package com.itheima.xiaotuxian.vo.member;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
 public class MemberCollectVo {
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
     * 收藏对象ids
     * 支持批量处理
     */
    private List<String> collectObjectIds;
    /**
     * 创建时间
     */
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime createTime;
}
