package com.itheima.xiaotuxian.service.record.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.record.RecordOrderSpu;
import com.itheima.xiaotuxian.mapper.record.RecordOrderSpuMapper;
import com.itheima.xiaotuxian.service.record.RecordOrderSpuService;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordOrderSpuServiceImpl extends ServiceImpl<RecordOrderSpuMapper, RecordOrderSpu> implements RecordOrderSpuService {

    @Override
    public List<HotGoodsVo> getHotGoods(HotGoodsQueryVo queryVo) {
        return this.baseMapper.getHotGoods(queryVo);
    }

    @Override
    public List<String> getSpuIdByMemberId(String memberId) {
        return this.baseMapper.getSpuIdByMemberId(memberId);
    }

}
