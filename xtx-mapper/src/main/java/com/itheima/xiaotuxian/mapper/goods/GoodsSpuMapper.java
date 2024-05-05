package com.itheima.xiaotuxian.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GoodsSpuMapper extends BaseMapper<GoodsSpu> {
    List<String> getFrontIdBySpuId(List<String> spuIdList);
}
