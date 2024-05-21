package com.itheima.xiaotuxian.mapper.classification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackend;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassificationBackendMapper extends BaseMapper<ClassificationBackend> {

    @Select("select c.id,c.name,m.url picture from classification_front c\n" +
            "inner join material_picture m\n" +
            "on c.picture_id = m.id\n" +
            "where c.pid=0")
    List<FrontResultVo> getCategoryParents();


    List<FrontResultVo> getCategoryOneFindTwo(String pid);


    List<GoodsItemResultVo> getCategoryGoodsForOneId(String pid);


}
