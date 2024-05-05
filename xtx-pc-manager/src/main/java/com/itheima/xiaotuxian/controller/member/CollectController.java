package com.itheima.xiaotuxian.controller.member;

import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import com.itheima.xiaotuxian.vo.BatchDeleteVo;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.CollectVo;
import com.itheima.xiaotuxian.vo.member.MemberCollectVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member/collect")
public class CollectController extends BaseController {
    @Autowired
    private UserMemberCollectService collectService;
    @Autowired
    private GoodsService spuService;
    @Autowired
    private MarketingRecommendService recommendService;

    /**
     * 获取收藏
     *
     * @param page        页码
     * @param pageSize    页尺寸
     * @param collectType 收藏类型
     * @return 收藏列表
     */
    @GetMapping
    public R<Pager<CollectVo>> findByPage(@RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(name = "collectType") Integer collectType) {
        var collectPage = collectService.findByPage(page, pageSize, collectType, getUserId());
        return R.ok(new Pager<CollectVo>(collectPage.getTotal()
                , collectPage.getSize()
                , collectPage.getPages()
                , collectPage.getCurrent()
                , collectPage.getRecords().stream().map(collectMap -> {
            var vo = BeanUtil.mapToBean(collectMap, CollectVo.class, true);
            vo.setCollectType(collectType);
            if (UserMemberStatic.COLLECT_TYPE_GOODS == collectType) {
                vo.setPicture(spuService.getSpuPicture(vo.getId(), CommonStatic.REQUEST_CLIENT_PC.equals(getClient()) ? CommonStatic.MATERIAL_SHOW_PC : CommonStatic.MATERIAL_SHOW_APP));
                vo.setPrice(spuService.getSpuPrice(vo.getId()));
                Optional.ofNullable(recommendService.findOne(vo.getId(),1)).ifPresent(recommend->vo.setDiscount(recommend.getDiscount()));
            }
            return vo;
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList())));
    }

    /**
     * 取消收藏
     *
     * @param vo 待删除信息
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public R<String> delete(@RequestBody BatchDeleteVo vo) {
        if (CollUtil.isNotEmpty(vo.getIds()) && vo.getType() != null) {
            vo.getIds().forEach(objectId -> collectService.remove(Wrappers.<UserMemberCollect>lambdaQuery()
                    .eq(UserMemberCollect::getMemberId, getUserId())
                    .eq(UserMemberCollect::getCollectObjectId, objectId)
                    .eq(UserMemberCollect::getCollectType, vo.getType())));
        }
        return R.ok();
    }

    /**
     * 添加收藏
     *
     * @param collectVo 收藏信息
     * @return 操作结果
     */
    @PostMapping
    public R<String> saveCollect(@RequestBody MemberCollectVo collectVo) {
        var userId = getUserId();
        Optional.ofNullable(collectVo.getCollectObjectIds()).ifPresentOrElse(collectObjectIds->
                collectObjectIds.forEach(collectObjectId->{
                            var source = collectService.getOne(Wrappers.<UserMemberCollect>lambdaQuery()
                                    .eq(UserMemberCollect::getCollectObjectId, collectObjectId)
                                    .eq(UserMemberCollect::getMemberId, userId)
                                    .eq(UserMemberCollect::getCollectType, collectVo.getCollectType()));
                            if (source == null) {
                                var collect = BeanUtil.toBean(collectVo,UserMemberCollect.class);
                                collect.setMemberId(userId);
                                collect.setCollectObjectId(collectObjectId);
                                collectService.save(collect);
                            }else{
                                log.warn("当前类型已经收藏，不需要再继续新增了");
                            }
                        }
                ),()->{
                    throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
                }
        );
        return R.ok();
    }
}