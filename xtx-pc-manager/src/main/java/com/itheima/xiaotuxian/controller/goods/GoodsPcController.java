package com.itheima.xiaotuxian.controller.goods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.OrderStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.goods.GoodsService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.member.UserMemberBrowseHistoryService;
import com.itheima.xiaotuxian.service.order.OrderSkuEvaluateService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.goods.SkuSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsSpecVo;
import com.itheima.xiaotuxian.vo.goods.goods.SaleGoodsAppDetailVo;
import com.itheima.xiaotuxian.vo.goods.goods.SaleGoodsDetailVo;
import com.itheima.xiaotuxian.vo.order.EvaluateQueryVo;
import com.itheima.xiaotuxian.vo.order.EvaluateSummaryVo;
import com.itheima.xiaotuxian.vo.order.EvaluateTagVo;
import com.itheima.xiaotuxian.vo.order.OrderEvaluateVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zsf
 * @date 2023年5月6日10:18:02
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsPcController extends BaseController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private MarketingRecommendService recommendService;
    @Autowired
    private OrderSkuEvaluateService skuEvaluateService;
    @Autowired
    private UserMemberBrowseHistoryService browseHistoryService;

    @Value("${test.recommendsJson}")
    private String recommendsJson;
    @Value("${test.similarProductsJson}")
    private String similarProductsJson;

    @Value("${test.hotByDayJson}")
    private String hotByDayJson;
    @Value("${test.evaluationInfoJson}")
    private String evaluationInfoJson;

    /**
     * 同类推荐(也支持猜你喜欢)
     *
     * @param id    商品id
     * @param limit 数量限制
     * @return 商品列表
     */
    @GetMapping("/relevant")
    public R<List<GoodsItemResultVo>> getRelevant(@RequestParam(name = "id", required = false) String id
            , @RequestParam(name = "limit", defaultValue = "4") Integer limit) {
        var page = 1;
        var pageSize = limit;
        return R.ok(searchGoodsService.findAllGoodsWithPublishTime(page, pageSize, "desc", false)
                .stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
    }

    /**
     * 商品信息-库存价格
     *
     * @param id sku id
     * @return 商品库存价格
     */
    @GetMapping("/stock/{id}")
    public R<SkuSimpleVo> getGoodsSimple(@PathVariable(name = "id") String id) {
        var sku = goodsService.findSkuById(id);
        if (sku == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var recommend = recommendService.findOne(sku.getSpuId(), 1);
        var result = new SkuSimpleVo();
        result.setNowPrice(sku.getSellingPrice());
        if (recommend != null) {
            result.setOldPrice(sku.getSellingPrice());
            result.setNowPrice(sku.getSellingPrice().multiply(recommend.getDiscount()));
            result.setNowPrice(result.getNowPrice().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP));
            result.setDiscount(recommend.getDiscount());
        }
        result.setStock(sku.getSaleableInventory());
        var goodsStateValid = new AtomicBoolean(false);
        //出售中或者仓库中的，审核通过的，编辑状态是提交的商品，且库存大于0的才是有效的商品
        Optional.ofNullable(goodsSpuService.getById(sku.getSpuId())).ifPresent(spu -> {
            goodsStateValid.set((GoodsStatic.STATE_STORING == spu.getState()
                    || GoodsStatic.STATE_SELLING == spu.getState())
                    && GoodsStatic.AUDIT_STATE_PASS == spu.getAuditState()
                    && GoodsStatic.EDIT_STATE_SUBMIT == spu.getEditState());
        });
        result.setIsEffective(goodsStateValid.get() && result.getStock() > 0);
        return R.ok(result);
    }

    /**
     * 商品信息-sku
     *
     * @param id sku id
     * @return sku信息
     */
    @GetMapping("/sku/{id}")
    public R<GoodsSpecVo> getSku(@PathVariable(name = "id") String id) {
        var sku = goodsService.findSkuById(id);
        if (sku == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        return R.ok(goodsService.findGoodsSpecById(sku.getSpuId(), getClient()));
    }

    /**
     * 评价信息
     *
     * @return 评价综述信息
     */
    @GetMapping("/{id}/evaluate")
    public R<EvaluateSummaryVo> getEvaluateSummary(@PathVariable(name = "id") String id) {
        var summaryVo = new EvaluateSummaryVo(0, new BigDecimal(0), 0, 0, null);
        Optional.ofNullable(goodsSpuService.getById(id)).ifPresent(spu -> {
            summaryVo.setSalesCount(spu.getSalesCount());
            var evaluateCount = skuEvaluateService.countEvaluate(spu.getId(), new BigDecimal(0), false);
            var praiseCount = skuEvaluateService.countEvaluate(spu.getId(), new BigDecimal(4), false);
            var hasPictureCount = skuEvaluateService.countEvaluate(spu.getId(), new BigDecimal(0), true);
            summaryVo.setEvaluateCount(evaluateCount);
            summaryVo.setHasPictureCount(hasPictureCount);
            var bPraiseCount = new BigDecimal(praiseCount * 100);
            var bEvaluateCount = new BigDecimal(evaluateCount);
            summaryVo.setPraisePercent(evaluateCount > 0 ? bPraiseCount.divide(bEvaluateCount, 1, RoundingMode.HALF_UP) : new BigDecimal(0));
            // 标签统计
            summaryVo.setTags(OrderStatic.getEvaluateTags().stream()
                    .map(tag -> new EvaluateTagVo(tag, skuEvaluateService.countEvaluateByTag(tag)))
                    .sorted(Comparator.comparing(EvaluateTagVo::getTagCount).reversed())
                    .collect(Collectors.toList()));
        });
        return R.ok(summaryVo);
    }

    /**
     * 评价分页数据
     *
     * @param queryVo 查询条件
     * @return 评价分页数据
     */
    @GetMapping("/{id}/evaluate/page")
    public R<Pager<OrderEvaluateVo>> getEvaluateByPage(@PathVariable(name = "id") String id, EvaluateQueryVo queryVo) {
        queryVo.setSpuId(id);
        return R.ok(new Pager<>(skuEvaluateService.findByPage(queryVo)));
    }

    /**
     * 商品详情
     *
     * @param id 商品Id
     * @return 商品详情
     */
    @GetMapping
    public R<SaleGoodsDetailVo> getGoodsDetails(@RequestParam(name = "id") String id) {
//    @GetMapping("/{id}")
//    public R<SaleGoodsDetailVo> getGoodsDetails(@PathVariable String id) {
        var result = goodsService.findSaleGoodsDetailsById(id, getClient(), Boolean.TRUE.equals(hasToken()) ? getUserId() : null);
        // 处理推荐数据
        if (CommonStatic.REQUEST_CLIENT_APP.equals(getClient())) {
            // 仅在请求为app时，添加此数据
            var goodsQueryVo = new GoodsQueryPageVo();
            goodsQueryVo.setId(result.getId());//TODO 没啥作用
            goodsQueryVo.setPage(1);
            goodsQueryVo.setPageSize(10);
            result.setRecommends(searchGoodsService.findAllGoods(goodsQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        }
        // 处理浏览记录
        Stream.of(hasToken()).filter(Boolean.TRUE::equals).forEach(hasToken -> browseHistoryService.saveHistory(getUserId(), id));
        // 处理评价信息
        var evaluateQueryVo = new EvaluateQueryVo();
        evaluateQueryVo.setSpuId(id);
        Optional.ofNullable(skuEvaluateService.findByPage(evaluateQueryVo).getRecords()).filter(CollUtil::isNotEmpty).ifPresent(records -> {
            var evaluationInfo = records.get(0);
            var praiseCount = skuEvaluateService.countEvaluate(id, new BigDecimal(4), false);
            var bPraiseCount = new BigDecimal(praiseCount * 100);
            evaluationInfo.setPraisePercent(praiseCount > 0 ? bPraiseCount.divide(bPraiseCount, 1, RoundingMode.HALF_UP) : new BigDecimal(0));
            result.setEvaluationInfo(evaluationInfo);
        });
        // 处理同类商品
        var similarQueryVo = new HotGoodsQueryVo();
        similarQueryVo.setLimit(4);
        similarQueryVo.setSpuId(id);
        result.setSimilarProducts(searchGoodsService.getHotGoods(similarQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        // 处理24小时热销
        var hotByDayQueryVo = new HotGoodsQueryVo();
        hotByDayQueryVo.setLimit(4);
        hotByDayQueryVo.setStartTime(LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.setHotByDay(searchGoodsService.getHotGoods(hotByDayQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        return R.ok(result);
    }

    /**
     * 商品详情-app
     *
     * @param id 商品Id
     * @return 商品详情
     */
    @GetMapping("/app")
    public R<SaleGoodsAppDetailVo> getGoodsAppDetails(@RequestParam(name = "id") String id) {
        var detailResult = new SaleGoodsDetailVo();
        try {
            detailResult = goodsService.findSaleGoodsDetailsById(id, getClient(), Boolean.TRUE.equals(hasToken()) ? getUserId() : null);
        } catch (Exception e) {
            detailResult = goodsService.findSaleGoodsDetailsById(id, getClient(), null);

        }
        var result = BeanUtil.toBean(detailResult, SaleGoodsAppDetailVo.class);
        // 2023年11月5日17:24:22 提出和结算页返回的地址信息不同，所以在此特殊处理下
        Optional.ofNullable(result.getUserAddresses()).ifPresentOrElse(address -> {
        }, () -> {
            result.setUserAddresses(new ArrayList<>());
        });
        // 处理推荐数据
        // 仅在请求为app时，添加此数据
        var goodsQueryVo = new GoodsQueryPageVo();
        goodsQueryVo.setId(result.getId());
        goodsQueryVo.setPage(1);
        goodsQueryVo.setPageSize(10);
        // 处理热门推荐商品
        // List<GoodsItemResultVo> recommends = JSON.parseObject(recommendsJson, new TypeReference<ArrayList<GoodsItemResultVo>>(){});
        result.setRecommends(searchGoodsService.findAllGoods(goodsQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        // 处理浏览记录
        // Stream.of(hasToken()).filter(Boolean.TRUE::equals).forEach(hasToken -> browseHistoryService.saveHistory(getUserId(), id));
        // 处理评价信息 TODO 暂时使用配置文件的json数据，方便录制视频
        var evaluateQueryVo = new EvaluateQueryVo();
        evaluateQueryVo.setSpuId(id);
        OrderEvaluateVo evaluation = JSON.parseObject(evaluationInfoJson, new TypeReference<OrderEvaluateVo>() {
        });

        result.setEvaluationInfo(evaluation);

        // 处理同类商品
        var similarQueryVo = new HotGoodsQueryVo();
        similarQueryVo.setLimit(3);
        similarQueryVo.setSpuId(id);

        // List<GoodsItemResultVo> similarList = JSON.parseObject(similarProductsJson, new TypeReference<ArrayList<GoodsItemResultVo>>(){});
        // result.setSimilarProducts(similarList);
        result.setSimilarProducts(searchGoodsService.getHotGoods(similarQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        // 处理24小时热销 TODO 暂时使用配置文件的json数据
        var hotByDayQueryVo = new HotGoodsQueryVo();
        hotByDayQueryVo.setLimit(3);
        hotByDayQueryVo.setStartTime(LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 处理24小时热销 TODO 暂时使用配置文件的json数据
        // List<GoodsItemResultVo> hotByDays = JSON.parseObject(hotByDayJson, new TypeReference<ArrayList<GoodsItemResultVo>>(){});
        // result.setHotByDay(hotByDays);
        result.setHotByDay(searchGoodsService.getHotGoods(hotByDayQueryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
        return R.ok(result);
    }

    /**
     * 热销推荐
     *
     * @param id    商品ID
     * @param limit 数量限制
     * @param type  热销类型，1为24小时，2为周榜，3为总榜，默认为1
     * @return R
     */
    @GetMapping("/hot")
    public R<List<GoodsItemResultVo>> getHot(@RequestParam(name = "id", required = false) String id
            , @RequestParam(name = "limit", defaultValue = "4") Integer limit
            , @RequestParam(name = "type", defaultValue = "1") Integer type) {
        var queryVo = new HotGoodsQueryVo();
        queryVo.setLimit(limit);
        queryVo.setSpuId(id);
        // 24小时
        Stream.of(type).filter(t -> t == 1).forEach(t -> queryVo.setStartTime(LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        // 周榜
        Stream.of(type).filter(t -> t == 2).forEach(t -> queryVo.setStartTime(LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return R.ok(searchGoodsService.getHotGoods(queryVo).stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList()));
    }
}
