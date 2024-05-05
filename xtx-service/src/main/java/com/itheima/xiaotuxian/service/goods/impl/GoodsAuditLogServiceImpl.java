package com.itheima.xiaotuxian.service.goods.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.goods.GoodsAuditLog;
import com.itheima.xiaotuxian.mapper.goods.GoodsAuditLogMapper;
import com.itheima.xiaotuxian.service.goods.GoodsAuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:22 下午
 * @Description:
 */
@Service
public class GoodsAuditLogServiceImpl extends ServiceImpl<GoodsAuditLogMapper, GoodsAuditLog> implements GoodsAuditLogService {
    @Override
    public List<GoodsAuditLog> findAll(String spuId) {
        var lambdaQueryChainWrapper = lambdaQuery();
        if (StrUtil.isNotEmpty(spuId)) {
            lambdaQueryChainWrapper.eq(GoodsAuditLog::getSpuId, spuId);
        }
        return lambdaQueryChainWrapper.list();
    }
}
