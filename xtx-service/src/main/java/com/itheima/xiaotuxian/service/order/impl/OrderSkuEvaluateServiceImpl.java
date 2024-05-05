package com.itheima.xiaotuxian.service.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.OrderSkuEvaluate;
import com.itheima.xiaotuxian.entity.order.OrderSkuProperty;
import com.itheima.xiaotuxian.mapper.order.OrderSkuEvaluateMapper;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuEvaluateService;
import com.itheima.xiaotuxian.service.order.OrderSkuPropertyService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.vo.goods.goods.SkuSpecVo;
import com.itheima.xiaotuxian.vo.member.EvaluateMemberSimpleVo;
import com.itheima.xiaotuxian.vo.order.EvaluateOrderInfoVo;
import com.itheima.xiaotuxian.vo.order.EvaluateQueryVo;
import com.itheima.xiaotuxian.vo.order.OrderEvaluateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderSkuEvaluateServiceImpl extends ServiceImpl<OrderSkuEvaluateMapper, OrderSkuEvaluate> implements OrderSkuEvaluateService {
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderSkuService orderSkuService;
    @Autowired
    private OrderSkuPropertyService orderSkuPropertyService;

    @Override
    public Integer countEvaluate(String spuId, BigDecimal score, Boolean hasPicture) {
        return count(Wrappers.<OrderSkuEvaluate>lambdaQuery()
                .eq(OrderSkuEvaluate::getSpuId, spuId)
                .ge(score != null, OrderSkuEvaluate::getScore, score)
                .isNotNull(hasPicture != null && hasPicture, OrderSkuEvaluate::getPictures));
    }

    @Override
    public Integer countEvaluateByTag(String tag) {
        return count(Wrappers.<OrderSkuEvaluate>lambdaQuery().like(OrderSkuEvaluate::getTags, tag));
    }

    @Override
    public Page<OrderEvaluateVo> findByPage(EvaluateQueryVo queryVo) {
        var page = new Page<OrderSkuEvaluate>(queryVo.getPage(), queryVo.getPageSize());
        var pageData = page(page, Wrappers.<OrderSkuEvaluate>lambdaQuery()
                .isNotNull(queryVo.getHasPicture() != null && queryVo.getHasPicture(), OrderSkuEvaluate::getPictures)
                .eq(StrUtil.isNotEmpty(queryVo.getSpuId()),OrderSkuEvaluate::getSpuId,queryVo.getSpuId())
                .like(StrUtil.isNotEmpty(queryVo.getTag()), OrderSkuEvaluate::getTags, queryVo.getTag())
                .orderBy(true, StrUtil.isNotEmpty(queryVo.getSortMethod()) && "asc".equalsIgnoreCase(queryVo.getSortMethod())
                        , StrUtil.isNotEmpty(queryVo.getSortField()) && "praiseCount".equals(queryVo.getSortField()) ? OrderSkuEvaluate::getPraiseCount : OrderSkuEvaluate::getCreateTime)
        );
        var resultData = new Page<OrderEvaluateVo>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        resultData.setRecords(pageData.getRecords().stream().map(evaluate -> {
            var vo = BeanUtil.toBean(evaluate, OrderEvaluateVo.class);
            // 处理订单信息
            Optional.ofNullable(orderSkuService.getOne(Wrappers.<OrderSku>lambdaQuery().eq(OrderSku::getOrderId, evaluate.getOrderId()).eq(OrderSku::getSkuId, evaluate.getSkuId())))
                    .ifPresent(orderSku -> {
                        var orderInfo = new EvaluateOrderInfoVo();
                        Optional.ofNullable(orderService.getById(orderSku.getOrderId())).ifPresent(order -> orderInfo.setCreateTime(order.getCreateTime()));
                        orderInfo.setQuantity(orderSku.getQuantity());
                        // 处理规格信息
                        var specs = new ArrayList<SkuSpecVo>();
                        orderSkuPropertyService.list(Wrappers.<OrderSkuProperty>lambdaQuery().eq(OrderSkuProperty::getOrderSkuId, orderSku.getId())).forEach(orderSkuProperty -> {
                            var spec = new SkuSpecVo();
                            spec.setName(orderSkuProperty.getPropertyMainName());
                            spec.setValueName(orderSkuProperty.getPropertyValueName());
                            specs.add(spec);
                        });
                        orderInfo.setSpecs(specs);
                        vo.setOrderInfo(orderInfo);
                    });
            // 处理评论者信息
            Optional.ofNullable(evaluate.getAnonymous()).filter(Boolean.FALSE::equals).ifPresent(anonymous ->
                    Optional.ofNullable(evaluate.getMemberId()).filter(StrUtil::isNotEmpty).ifPresent(memberId ->
                            Optional.ofNullable(userMemberService.getById(memberId)).ifPresent(userMember -> vo.setMember(BeanUtil.toBean(userMember, EvaluateMemberSimpleVo.class)))));
            // 处理标签
            Optional.ofNullable(evaluate.getTags()).filter(StrUtil::isNotEmpty).ifPresent(strtags -> vo.setTags(Arrays.stream(strtags.split(",")).collect(Collectors.toList())));
            // 处理图片
            Optional.ofNullable(evaluate.getPictures()).filter(StrUtil::isNotEmpty).ifPresent(strPictures -> vo.setPictures(Arrays.stream(strPictures.split(",")).collect(Collectors.toList())));
            return vo;
        }).collect(Collectors.toList()));
        return resultData;
    }
}
