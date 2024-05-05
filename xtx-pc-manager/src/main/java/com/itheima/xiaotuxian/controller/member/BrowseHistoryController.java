package com.itheima.xiaotuxian.controller.member;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMemberBrowseHistory;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberBrowseHistoryService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.BatchDeleteVo;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.member.BrowseHistoryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/member/browseHistory")
public class BrowseHistoryController extends BaseController {
    @Autowired
    private UserMemberBrowseHistoryService browseHistoryService;
    @Autowired
    private GoodsService spuService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private MarketingRecommendService recommendService;

    /**
     * 获取我的足迹
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public R<Pager<BrowseHistoryVo>> findByPage(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        var pageResult = new Page<UserMemberBrowseHistory>(page, pageSize);
        var historyPage = browseHistoryService.page(pageResult
                , Wrappers
                        .<UserMemberBrowseHistory>lambdaQuery()
                        // 当前用户
                        .eq(UserMemberBrowseHistory::getMemberId, getUserId())
                        // 一个月以内
                        .ge(UserMemberBrowseHistory::getUpdateTime, DateUtil.offsetMonth(new Date(), -1).toString())
                        // 按更新时间倒序
                        .orderByDesc(UserMemberBrowseHistory::getUpdateTime));
        return R.ok(new Pager<BrowseHistoryVo>(historyPage.getTotal()
                , historyPage.getSize()
                , historyPage.getPages()
                , historyPage.getCurrent()
                , historyPage.getRecords().stream().map(history -> {
            var arVo = new AtomicReference<BrowseHistoryVo>();
            Optional.ofNullable(goodsSpuService.getById(history.getSpuId()))
                    .ifPresent(spu -> {
                        var vo = new BrowseHistoryVo();
                        vo.setId(history.getId());
                        vo.setSpu(ConvertUtil.convertGoodsItem(spu, spuService.getSpuPicture(spu.getId(), getShowClient()), null, spuService.getSpuPrice(spu.getId())));
                        Optional.ofNullable(vo.getSpu()).map(GoodsItemResultVo::getId).flatMap(spuId -> Optional.ofNullable(recommendService.findOne(spuId, 1))).ifPresent(recommend -> vo.getSpu().setDiscount(recommend.getDiscount()));
                        arVo.set(vo);
                    });
            return arVo.get();
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList())));
    }

    /**
     * 删除我的足迹
     *
     * @param vo 待删除信息
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public R<String> delete(@RequestBody BatchDeleteVo vo) {
        // 操作顺序，ids不为空时，仅ids参数生效。ids为空时，all参数生效
        var userId = getUserId();
        // ids不为空
        if (CollUtil.isNotEmpty(vo.getIds())) {
            vo.getIds().forEach(spuId -> browseHistoryService.remove(Wrappers.<UserMemberBrowseHistory>lambdaQuery().eq(UserMemberBrowseHistory::getMemberId, userId).eq(UserMemberBrowseHistory::getSpuId, spuId)));
        }
        // all参数标记为true
        if (vo.getAll() != null && vo.getAll()) {
            browseHistoryService.remove(Wrappers.<UserMemberBrowseHistory>lambdaQuery().eq(UserMemberBrowseHistory::getMemberId, userId));
        }
        return R.ok();
    }
}
