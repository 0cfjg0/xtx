package com.itheima.xiaotuxian.service.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.goods.GoodsAuditLog;

import java.util.List;

public interface GoodsAuditLogService extends IService<GoodsAuditLog> {
    /**
     * 根据条件获取商品审核日志
     *
     * @param spuId 商品id
     * @return 商品审核日志
     */
    List<GoodsAuditLog> findAll(String spuId);
}
