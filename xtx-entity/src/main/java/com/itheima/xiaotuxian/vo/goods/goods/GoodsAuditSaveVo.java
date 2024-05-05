package com.itheima.xiaotuxian.vo.goods.goods;

import lombok.Data;

import java.util.List;

@Data
public class GoodsAuditSaveVo {
    private String id;

    private List<String> ids;
    /**
     * 商品审核状态，2为审核通过，3为驳回
     */
    private Integer auditState;
    /**
     * 驳回原因，仅在驳回时使用
     */
    private String rejectDecription;
}
