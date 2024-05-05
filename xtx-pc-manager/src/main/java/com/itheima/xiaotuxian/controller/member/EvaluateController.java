package com.itheima.xiaotuxian.controller.member;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.order.OrderSku;
import com.itheima.xiaotuxian.entity.order.OrderSkuEvaluate;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuEvaluateService;
import com.itheima.xiaotuxian.service.order.OrderSkuPropertyService;
import com.itheima.xiaotuxian.service.order.OrderSkuService;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.evaluate.EvaluateGoodsVo;
import com.itheima.xiaotuxian.vo.evaluate.EvaluateSaveVo;
import com.itheima.xiaotuxian.vo.evaluate.EvaluateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/evaluate")
public class EvaluateController extends BaseController {
    @Autowired
    private OrderSkuEvaluateService orderSkuEvaluateService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderSkuService orderSkuService;
    @Autowired
    private OrderSkuPropertyService orderSkuPropertyService;

    private static final String[] TAGS = {"穿着舒服", "漂亮精致", "保暖性好", "尺寸合适", "性价比高", "质量上乘", "值得拥有", "快递给力", "材质很薄"};

    /**
     * 获取待评价订单信息
     *
     * @param id 订单id
     * @return 待评价订单信息
     */
    @GetMapping("/order")
    public R<List<EvaluateGoodsVo>> getPendingEvaluateInfo(@RequestParam(name = "id") String id) {
        var order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!OrderStatic.STATE_PENDING_EVALUATION.equals(order.getOrderState())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_STATE_VALID);
        }
        var results = new ArrayList<EvaluateGoodsVo>();
        orderSkuService.list(Wrappers.<OrderSku>lambdaQuery().eq(OrderSku::getOrderId, order.getId())).forEach(orderSku -> {
            var vo = new EvaluateGoodsVo();
            vo.setId(orderSku.getSpuId());
            vo.setName(orderSku.getName());
            vo.setPicture(orderSku.getImage());
            vo.setCount(orderSku.getQuantity());
            vo.setSkuId(orderSku.getSkuId());
            vo.setPrice(orderSku.getCurPrice());
            vo.setPayPrice(orderSku.getRealPay());
            vo.setTotalPrice(orderSku.getCurPrice().multiply(new BigDecimal(orderSku.getQuantity())));
            vo.setTotalPayPrice(orderSku.getRealPay().multiply(new BigDecimal(orderSku.getQuantity())));
            vo.setTags(Arrays.asList(TAGS));
            // 获取商品规格信息
            vo.setAttrsText(orderSkuPropertyService.getOrderAttrsText(order.getId(), orderSku.getSkuId(), getClient(), null));
            results.add(vo);
        });
        return R.ok(results);
    }

    /**
     * 发布评价
     * 订单改成已完成的状态
     * @param id     订单id
     * @param saveVo 评价信息
     * @return 操作结果
     */
    @PostMapping("/{id}")
    public R<String> saveEvaluate(@PathVariable(name = "id") String id, @RequestBody EvaluateSaveVo saveVo) {
        var order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!OrderStatic.STATE_PENDING_EVALUATION.equals(order.getOrderState())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_STATE_VALID);
        }
        var anonymous = saveVo.getAnonymous();
        var userId = getUserId();
        saveVo.getEvaluates().forEach(itemVo -> {
            var evaluate = new OrderSkuEvaluate();
            evaluate.setSpuId(itemVo.getId());
            evaluate.setSkuId(itemVo.getSkuId());
            evaluate.setScore(itemVo.getScore());
            evaluate.setMemberId(userId);
            evaluate.setOrderId(id);
            evaluate.setContent(itemVo.getContent());
            Optional.ofNullable(itemVo.getTags()).filter(CollUtil::isNotEmpty).ifPresent(tags ->
                    evaluate.setTags(String.join(",", tags)));
            Optional.ofNullable(itemVo.getPictures()).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                    evaluate.setPictures(String.join(",", pictures)));
            evaluate.setPraiseCount(RandomUtil.randomInt(0, 1000));
            evaluate.setAnonymous(anonymous);
            evaluate.setCreator(userId);
            orderSkuEvaluateService.save(evaluate);
        });
        //设置完成评价的时间
        order.setEvaluationTime(LocalDateTime.now());
        order.setOrderState(OrderStatic.STATE_FINISH);
        orderService.updateById(order);
        return R.ok();
    }

    /**
     * 获取评价信息
     *
     * @param id 订单id
     * @return 评价信息
     */
    @GetMapping
    public R<EvaluateVo> getEvaluate(@RequestParam(name = "id") String id) {
        var order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!OrderStatic.STATE_FINISH.equals(order.getOrderState())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_STATE_VALID);
        }
        var results = new ArrayList<EvaluateVo>();
        orderSkuService.list(Wrappers.<OrderSku>lambdaQuery().eq(OrderSku::getOrderId, order.getId())).forEach(orderSku -> {
            var vo = new EvaluateVo();
            vo.setId(orderSku.getSpuId());
            vo.setName(orderSku.getName());
            vo.setPicture(orderSku.getImage());
            vo.setCount(orderSku.getQuantity());
            vo.setSkuId(orderSku.getSkuId());
            vo.setPrice(orderSku.getCurPrice());
            vo.setPayPrice(orderSku.getRealPay());
            vo.setTotalPrice(orderSku.getCurPrice().multiply(new BigDecimal(orderSku.getQuantity())));
            vo.setTotalPayPrice(orderSku.getRealPay().multiply(new BigDecimal(orderSku.getQuantity())));
            // 获取商品规格信息
            vo.setAttrsText(orderSkuPropertyService.getOrderAttrsText(order.getId(), orderSku.getSkuId(),getClient(), null));
            // 处理评价信息
            Optional.ofNullable(orderSkuEvaluateService.list(Wrappers.<OrderSkuEvaluate>lambdaQuery()
                    .eq(OrderSkuEvaluate::getOrderId, orderSku.getOrderId())
                    .eq(OrderSkuEvaluate::getSkuId, orderSku.getSkuId())
                    .eq(OrderSkuEvaluate::getSpuId, orderSku.getSpuId())))
                    .filter(CollUtil::isNotEmpty).ifPresent(evaluates -> {
                var evaluate = evaluates.get(0);
                Optional.ofNullable(evaluate.getTags()).filter(StrUtil::isNotEmpty).ifPresent(tags ->
                        vo.setTags(Arrays.asList(tags.split(","))));
                vo.setContent(evaluate.getContent());
                vo.setScore(evaluate.getScore());
                Optional.ofNullable(evaluate.getPictures()).filter(StrUtil::isNotEmpty).ifPresent(pictures ->
                        vo.setPictures(Arrays.asList(pictures.split(","))));
            });
            results.add(vo);
        });
        return R.ok(results);
    }
}
