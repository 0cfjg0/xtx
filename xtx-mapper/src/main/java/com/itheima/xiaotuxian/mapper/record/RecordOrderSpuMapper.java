package com.itheima.xiaotuxian.mapper.record;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.record.RecordOrderSpu;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecordOrderSpuMapper extends BaseMapper<RecordOrderSpu> {
    List<HotGoodsVo> getHotGoods(@Param("queryVo") HotGoodsQueryVo queryVo);

    List<String> getSpuIdByMemberId(@Param("memberId") String memberId);
}
