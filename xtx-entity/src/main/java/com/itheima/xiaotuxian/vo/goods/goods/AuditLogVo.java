package com.itheima.xiaotuxian.vo.goods.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogVo {
    /**
     * 审核信息id
     */
    private String id;
    /**
     * 审核类型，2为审核通过，3为驳回
     */
    private Integer auditType;
    /**
     * 驳回说明
     */
    private String rejectDecription;
    /**
     * 提交人信息
     */
    private String commiter;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
