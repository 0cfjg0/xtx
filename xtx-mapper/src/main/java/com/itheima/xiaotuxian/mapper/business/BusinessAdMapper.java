package com.itheima.xiaotuxian.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.xiaotuxian.entity.business.BusinessAd;
import com.itheima.xiaotuxian.vo.home.response.BannerResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusinessAdMapper extends BaseMapper<BusinessAd> {

@Select("select id,banner_url as imgUrl,target_url as hrefUrl,target_type as type from business_ad where distribution_site=#{distributionSite}  limit 4")
    List<BannerResultVo> getbanner( Integer distributionSite);
}
