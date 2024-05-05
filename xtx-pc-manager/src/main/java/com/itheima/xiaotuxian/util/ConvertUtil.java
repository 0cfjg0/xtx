package com.itheima.xiaotuxian.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.home.response.RecommendVo;
import com.itheima.xiaotuxian.vo.member.response.LoginResultVo;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConvertUtil {
    private ConvertUtil() {
    }

    /**
     * 登录信息转换
     *
     * @param userMember 用户信息
     * @param jwt        令牌
     * @return 登录信息
     */
    public static LoginResultVo convertLoginInfo(UserMember userMember, String jwt) {
        var result = new LoginResultVo();
        result.setId(userMember.getId());
        result.setMobile(userMember.getMobile());
        result.setToken(jwt);
        result.setAccount(userMember.getAccount());
        result.setAvatar(userMember.getAvatar());
        result.setNickname(userMember.getNickname());
        result.setGender(userMember.getGender());
        if (userMember.getBirthday() != null) {
            result.setBirthday(userMember.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        result.setCityCode(userMember.getCityCode());
        result.setProvinceCode(userMember.getProvinceCode());
        result.setProfession(userMember.getProfession());
        return result;
    }

    /**
     * 商品信息转换
     *
     * @param spu      spu信息
     * @param picture  商品图片
     * @param discount 折扣
     * @param price    商品价格
     * @return 商品信息
     */
    public static GoodsItemResultVo convertGoodsItem(GoodsSpu spu, String picture, BigDecimal discount, BigDecimal price) {
        var vo = new GoodsItemResultVo();
        vo.setId(spu.getId());
        vo.setDesc(spu.getPcDecription());
        vo.setName(spu.getName());
        vo.setPicture(picture);
        vo.setDiscount(discount);
        vo.setPrice(price);
        return vo;
    }

    /**
     * 首页商品信息转换
     *
     * @param esGoods 搜索引擎中的商品
     * @param client  当前请求客户端
     * @return 商品信息
     */
    public static GoodsItemResultVo convertHomeGoods(EsGoods esGoods, String client) {
        var result = BeanUtil.toBean(esGoods, GoodsItemResultVo.class);
        result.setPicture(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? esGoods.getPcPicture() : esGoods.getAppPicture());
        result.setDesc(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? esGoods.getPcDecription() : esGoods.getAppDecription());
        return result;
    }

    /**
     * 获取推荐数据列表
     *
     * @return 推荐数据
     */
    public static List<RecommendVo> getRecommends() {
        return ListUtil.toList(
                new RecommendVo("https://yanxuan-item.nosdn.127.net/1d422d423100c7efbd143c7adfe93e0e.png?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "https://yanxuan-item.nosdn.127.net/7f6470607bc86b134862458b2f086886.jpg?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "特惠推荐"
                        , "精选全攻略"),
                new RecommendVo("https://yanxuan-item.nosdn.127.net/43098fcb3cd8e20a70ee72b258670129.png?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "https://yanxuan-item.nosdn.127.net/35883b33a5a2e496f7971b360d750702.jpg?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "爆款推荐"
                        , "最受欢迎"),
                new RecommendVo("https://yanxuan-item.nosdn.127.net/460a48405ba7c2f12fa82601a9f49354.png?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "https://yanxuan-item.nosdn.127.net/9be0f0c16d7fbafacee94ecc2d995ee8.jpg?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "一站买全"
                        , "精心优选"),
                new RecommendVo("https://yanxuan-item.nosdn.127.net/524b34ceaf5b109bf2102b80370d05bf.png?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "https://yanxuan-item.nosdn.127.net/d0bc4db1de08868f3f7376caa9c51f98.png?type=webp&quality=95&thumbnail=245x245&imageView"
                        , "领劵中心"
                        , "超值优惠券")
        );
    }
}
