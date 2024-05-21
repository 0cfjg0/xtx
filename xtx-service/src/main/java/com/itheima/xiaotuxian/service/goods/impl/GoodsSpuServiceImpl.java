package com.itheima.xiaotuxian.service.goods.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.mapper.goods.GoodsSpuMapper;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.record.RecordOrderSpuService;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author: lvbencai
 * @Date: 2023年4月7日08:35:19
 * @Description:
 */
@Service
@Slf4j
public class GoodsSpuServiceImpl extends ServiceImpl<GoodsSpuMapper, GoodsSpu> implements GoodsSpuService {
    @Autowired
    private GoodsSpuMapper goodsSpuMapper;
    @Autowired
    private RecordOrderSpuService recordOrderSpuService;
//    @Autowired
//    private GoodsSpuService goodsSpuService;

    @Override
    public Integer countByBackend(String backendId) {
        return this.getBaseMapper().selectCount(Wrappers.<GoodsSpu>lambdaQuery().eq(GoodsSpu::getClassificationBackendId, backendId));
    }

    @Override
    public Page<GoodsSpu> getPageData(GoodsQueryPageVo queryVo, Page<GoodsSpu> pageResult, HashSet<String> frontBackEndIds) {
        Page<GoodsSpu> pageData = this.getBaseMapper().selectPage(pageResult, Wrappers.<GoodsSpu>lambdaQuery()
                .like(StrUtil.isNotEmpty(queryVo.getId()), GoodsSpu::getId, queryVo.getId())
                .like(StrUtil.isNotEmpty(queryVo.getName()), GoodsSpu::getName, queryVo.getName())
                .eq(StrUtil.isNotEmpty(queryVo.getBackendId()), GoodsSpu::getClassificationBackendId, queryVo.getBackendId())
                .eq(StrUtil.isNotEmpty(queryVo.getBan()), GoodsSpu::getId, queryVo.getBan())
                .in(CollUtil.isNotEmpty(frontBackEndIds), GoodsSpu::getClassificationBackendId, frontBackEndIds)
                .ge(Objects.nonNull(queryVo.getMinPrice()), GoodsSpu::getPrice, queryVo.getMinPrice())
                .le(Objects.nonNull(queryVo.getMaxPrice()), GoodsSpu::getPrice, queryVo.getMaxPrice())
                .ge(Objects.nonNull(queryVo.getMinSalesCount()), GoodsSpu::getSalesCount, queryVo.getMinSalesCount())
                .le(Objects.nonNull(queryVo.getMaxSalesCount()), GoodsSpu::getSalesCount, queryVo.getMaxSalesCount())
                .eq(Objects.nonNull(queryVo.getState()) && queryVo.getState() != 0, GoodsSpu::getState, queryVo.getState())
                //商品状态，1为出售中，2为仓库中，3为已售罄，4为回收站，5为历史宝贝 默认不查询 4 5状态的数据
                .lt(Objects.isNull(queryVo.getState()) || queryVo.getState() == 0, GoodsSpu::getState, 4)
                .eq(Objects.nonNull(queryVo.getAuditState()) && queryVo.getAuditState() != 0, GoodsSpu::getAuditState, queryVo.getAuditState())
                .eq(Objects.nonNull(queryVo.getEditState()), GoodsSpu::getEditState, queryVo.getEditState())
                .like(StrUtil.isNotEmpty(queryVo.getSpuCode()), GoodsSpu::getSpuCode, queryVo.getSpuCode())
                .ge(StrUtil.isNotBlank(queryVo.getStartTime()), GoodsSpu::getCreateTime,queryVo.getStartTime())
                .le(StrUtil.isNotBlank(queryVo.getEndTime()), GoodsSpu::getCreateTime, queryVo.getEndTime())
                // 增加默认排序 2022年3月29日11:57:21
                .orderByDesc(GoodsSpu::getCreateTime)
        );
        return pageData;
    }

    @Override
    public List<String> getFrontIdBySpuId(List<String> spuIdList) {
        return goodsSpuMapper.getFrontIdBySpuId(spuIdList);
    }

    /**
     * 根据用户标识memberId查询已购买商品的前台分类id列表图
     * @param memberId
     * @return frontIds
     */
    @Override()
    public List<String> getFrontIdsByMemberId (String memberId){
        if (StrUtil.isEmpty(memberId)) {
            log.info("未查到用户:{}的购买商品记录",memberId);
            return null;
        }
        List<String> spuIdList = recordOrderSpuService.getSpuIdByMemberId(memberId);
        if (CollUtil.isEmpty(spuIdList)){
            return null;
        }
        List<String> frontIdList = this.getFrontIdBySpuId(spuIdList);
        return frontIdList;
    }
    @Override
    public List<GoodsItemResultVo> getNewGoods() {
        System.out.println("----------------------------------------------13333-------------");

        List<GoodsItemResultVo> list = goodsSpuMapper.getNewGoods();
        System.out.println("----------------------------------------------13334-------------");

        return list;
    }


}
