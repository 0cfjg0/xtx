package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-审核记录
 */
@Data
@TableName(value = "goods_audit_log")
public class GoodsAuditLog extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * spu_id
     */
    private String spuId;
    /**
     * spu名称
     */
    private String spuName;
    /**
     * 提交人名称
     */
    private String commiter;
    /**
     * 审核类型，2为审核通过，3为驳回
     */
    private Integer auditType;
    /**
     * 驳回说明
     */
    private String rejectDecription;
}
