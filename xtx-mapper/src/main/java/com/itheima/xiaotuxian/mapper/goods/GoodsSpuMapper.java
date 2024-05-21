package com.itheima.xiaotuxian.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsSpuMapper extends BaseMapper<GoodsSpu> {
    List<String> getFrontIdBySpuId(List<String> spuIdList);

    @Select("select gs.id as id,mp.name as name,gs.name as 'desc',gs.price as price, mp.url as picture\n" +
            "             from goods_spu gs inner join goods_spu_picture gsp on gs.id = gsp.spu_id\n" +
            "                        inner join material_picture mp on gsp.picture_id = mp.id\n" +
            "              order by gs.update_time DESC limit 4 ")
    List<GoodsItemResultVo> getNewGoods();
}
