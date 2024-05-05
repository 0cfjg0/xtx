package com.itheima.xiaotuxian.service.search.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.enums.TuxianIndexEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.entity.classification.ClassificationFront;
import com.itheima.xiaotuxian.entity.goods.GoodsKeyword;
import com.itheima.xiaotuxian.entity.search.*;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontRelationService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.*;
import com.itheima.xiaotuxian.service.marketing.MarketingRecommendService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.order.OrderService;
import com.itheima.xiaotuxian.service.order.OrderSkuEvaluateService;
import com.itheima.xiaotuxian.service.record.RecordOrderSpuService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsQueryPageVo;
import com.itheima.xiaotuxian.vo.goods.keyword.KeywordQueryVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsQueryVo;
import com.itheima.xiaotuxian.vo.record.HotGoodsVo;
import com.itheima.xiaotuxian.vo.search.SearchConditionVo;
import com.itheima.xiaotuxian.vo.search.SearchGoodsServiceVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SearchGoodsServiceImpl implements SearchGoodsService {
    // @Autowired
    // private HighLevelUtil highLevelUtil;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private GoodsSkuPropertyValueService skuPropertyValueService;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private ClassificationFrontService frontService;
    @Autowired
    private ClassificationFrontRelationService frontRelationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderSkuEvaluateService skuEvaluateService;
    @Autowired
    private MarketingRecommendService marketingRecommendService;
    @Autowired
    private GoodsKeywordService keywordService;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private RecordOrderSpuService recordOrderSpuService;

    private static final String FIELD_PUBLISH_TIME = "publishTime";
    private static final String FIELD_FRONT_ID = "fronts.id";

    /**
     * 向es中保存商品信息
     * @param id 商品id
     * @return 是否保存成功
     */
    @Override
    public Boolean saveGoods(String id) {
        log.info("es-saveGoods,id:{}",id);
        Optional.ofNullable(goodsSpuService.getById(id))
                .ifPresent(goodsSpu -> {
                    var esGoods = BeanUtil.toBean(goodsSpu, EsGoods.class);
                    // 商品发布类型为立即发布时，将ES中的商品发布时间调整为当前时间
                    Stream.of(goodsSpu.getShelfType()).filter(shelfType -> GoodsStatic.SHELF_TYPE_IMMEDIATELY == shelfType)
                            .forEach(shelfType -> esGoods.setPublishTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))));
                    // 商品发布类型为定时发布时，则发布时间为后台输入的发布时间
                    Stream.of(goodsSpu.getShelfType()).filter(shelfType -> GoodsStatic.SHELF_TYPE_TIMING == shelfType)
                            .forEach(shelfType -> {
                                Optional.ofNullable(goodsSpu.getPublishTime()).ifPresent(publishTime->
                                        esGoods.setPublishTime(goodsSpu.getPublishTime().toEpochSecond(ZoneOffset.of("+8")))
                                );
                            });
                    var fronts = new ArrayList<EsFront>();
                    var frontRelations = new ArrayList<EsFrontRelation>();
                    //处理后台分类信息
                    Optional.ofNullable(goodsSpu.getClassificationBackendId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(backendId -> Optional.ofNullable(backendService.getById(backendId))
                                    .ifPresent(backend -> {
                                        var esBackend = BeanUtil.toBean(backend, EsBackend.class);
                                        esGoods.setBackend(esBackend);
                                        // 提取其关联的前台类目
                                        var tempIds = CollUtil.list(false, backendId);
                                        BackendSimpleVo simpleBackend = backendService.findBackendLink(backendId);
                                        Optional.ofNullable(simpleBackend.getParent())
                                                .ifPresent(parent -> {
                                                    tempIds.add(parent.getId());
                                                    Optional.ofNullable(parent.getParent())
                                                            .ifPresent(pparent -> tempIds.add(pparent.getId()));
                                                });
                                        var ids = tempIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
                                        frontRelationService.findAllByRelation(ids, FrontStatic.RELATION_TYPE_BACKEND).forEach(frontRelation -> {
                                            fronts.addAll(this.getEsFronts(frontService.getById(frontRelation.getFrontId())));
                                            frontRelations.add(BeanUtil.toBean(frontRelation, EsFrontRelation.class));
                                        });
                                    })
                            );
                    //处理品牌信息
                    Optional.ofNullable(goodsSpu.getBrandId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(brandId ->{
                                Optional.ofNullable(brandService.getById(brandId))
                                    .ifPresent(brand -> {
                                        var esBrand = BeanUtil.toBean(brand, EsBrand.class);
                                        esGoods.setBrand(esBrand);
                                        // 提取其关联的前台类目
                                        frontRelationService.findAllByRelation(Collections.singletonList(brandId), FrontStatic.RELATION_TYPE_BRAND).forEach(frontRelation -> {
                                            fronts.addAll(this.getEsFronts(frontService.getById(frontRelation.getFrontId())));
                                            frontRelations.add(BeanUtil.toBean(frontRelation, EsFrontRelation.class));
                                        });
                                    });
                            });
                    //处理商品图片
                    esGoods.setPcPicture(goodsService.getSpuPicture(esGoods.getId(), CommonStatic.MATERIAL_SHOW_PC));
                    esGoods.setAppPicture(goodsService.getSpuPicture(esGoods.getId(), CommonStatic.MATERIAL_SHOW_APP));
                    //处理库存 update by bencai.lv 20230312 增加空指针的判断
                    Optional.ofNullable(goodsService.findAllSku(esGoods.getId()))
                            .ifPresent(
                                    thisGoods->
                                            Optional.ofNullable(thisGoods).filter(CollUtil::isNotEmpty)
                                                    .ifPresent(
                                                            skus -> esGoods.setInventory(skus.stream().mapToInt(sku-> null == sku.getPhysicalInventory() ? 0:sku.getPhysicalInventory()).sum())
                                                    )
                            );
                    //处理销售属性信息
                    var propertyGroupIds = new ArrayList<String>();
                    Optional.ofNullable(skuPropertyValueService.findAllSkuPropertyValueBySpu(goodsSpu.getId()))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(skuPropertyValues -> {
                                esGoods.setSaleProperties(skuPropertyValues.stream()
                                        .map(skuPropertyValue -> BeanUtil.toBean(skuPropertyValue, EsPropertyValue.class))
                                        .collect(Collectors.toList()));
                                var propertyGroupNames = CollUtil.getFieldValues(skuPropertyValues, "propertyGroupName", String.class).stream().distinct().collect(Collectors.toList());
                                esGoods.setSales(CollUtil.join(propertyGroupNames, " "));
                                // 提取其关联的前台类目
                                propertyGroupIds.addAll(CollUtil.getFieldValues(skuPropertyValues, "propertyGroupId", String.class)
                                        .stream().filter(StrUtil::isNotEmpty).collect(Collectors.toList()));
                                var salePropertyGroupMap = new HashMap<String, List<String>>();
                                frontRelationService.findAllByRelation(propertyGroupIds, FrontStatic.RELATION_TYPE_SALE_PROPERTY_GROUP).forEach(frontRelation -> {
                                    fronts.addAll(this.getEsFronts(frontService.getById(frontRelation.getFrontId())));
                                    var frontIds = salePropertyGroupMap.get(frontRelation.getObjectId()) == null ? new ArrayList<String>() : salePropertyGroupMap.get(frontRelation.getObjectId());
                                    frontIds.add(frontRelation.getFrontId());
                                    salePropertyGroupMap.put(frontRelation.getObjectId(), frontIds);
                                    frontRelations.add(BeanUtil.toBean(frontRelation, EsFrontRelation.class));
                                });
                            });
                    //处理前台分类信息
                    esGoods.setFronts(
                                fronts.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EsFront::getId))), ArrayList::new)));
                    //处理前台分类关联关系信息
                    esGoods.setFrontRelations(frontRelations.stream()
                            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EsFrontRelation::getId))), ArrayList::new)));


                    //ES中应该存放后台类目信息，查询同类推荐商品的时候，根据后台类目的id信息查询

                    // 处理订单数量
                    esGoods.setOrderNum(orderService.countOrderBySpuId(goodsSpu.getId()).longValue());
                    // 处理评论数量
                    esGoods.setEvaluateNum(skuEvaluateService.countEvaluate(goodsSpu.getId(), new BigDecimal(0), false).longValue());
                    //处理折扣信息
                    Optional.ofNullable(marketingRecommendService.findOne(esGoods.getId(), 1))
                            .ifPresentOrElse(marketingRecommend -> {
                                        esGoods.setHasDiscount(true);
                                        esGoods.setDiscount(marketingRecommend.getDiscount());
                                        esGoods.setDiscountPrice(esGoods.getPrice().multiply(esGoods.getDiscount()));
                                        esGoods.setDiscountPrice(esGoods.getDiscountPrice().divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP));
                                    }
                                    , () -> esGoods.setHasDiscount(false));
                    // 处理关键字信息
                    var keywordQueryVo = new KeywordQueryVo();
                    AtomicBoolean ab = new AtomicBoolean(false);
                    Optional.ofNullable(esGoods.getBackend()).ifPresent(backend -> {
                        keywordQueryVo.setBackendId(backend.getId());
                        ab.set(true);
                    });
                    Optional.ofNullable(esGoods.getBrand()).ifPresent(brand -> {
                        keywordQueryVo.setBrandId(brand.getId());
                        ab.set(true);
                    });
                    Stream.of(propertyGroupIds).filter(CollUtil::isNotEmpty).forEach(ids -> {
                        keywordQueryVo.setPropertyGroupIds(propertyGroupIds);
                        ab.set(true);
                    });
                    Stream.of(ab.get()).filter(Boolean.TRUE::equals).forEach(isOp ->
                            esGoods.setHotKeys(keywordService.findAllByOr(keywordQueryVo)
                                    .stream().map(GoodsKeyword::getId)
                                    .collect(Collectors.toList()))
                    );

                    // 构建es请求
                    var request = new IndexRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName());
                    request.id(id);
                    request.source(JSONUtil.toJsonStr(esGoods), XContentType.JSON);
                    try {
                        /* highLevelUtil.getClient() */client.index(request, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        log.error(JSONUtil.toJsonStr(esGoods));
                        log.error(e.getMessage(),e);
                        throw new BusinessException(ErrorMessageEnum.ES_INSERT_FAILED);
                    }
                });
        return true;
    }

    /**
     * es删除商品
     * @param id 商品Id
     * @return
     */
    @Override
    public Boolean deleteGoods(String id) {
        log.info("从es删除spu,deleteGoods,id:{}",id);
        if (StrUtil.isNotEmpty(id)) {
            var request = new DeleteRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName(), id);
            try {
                /* highLevelUtil.getClient() */client.delete(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.ES_DELETE_FAILED);
            }
        }
        return true;
    }

    @Override
    public Long countByFrontRelation(String relationKey) {
        var al = new AtomicLong(0);
        var qb = QueryBuilders.termQuery("frontRelations.relationKey", relationKey);
        var countRequest = new CountRequest();
        countRequest.indices(TuxianIndexEnum.INDEX_GOODS.getIndexName());
        countRequest.query(qb);
        try {
            var countResponse = /* highLevelUtil.getClient() */client.count(countRequest, RequestOptions.DEFAULT);
            Stream.of(countResponse.status().getStatus()).filter(status -> status == HttpStatus.HTTP_OK).forEach(status -> al.set(countResponse.getCount()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return al.get();
    }

    @Override
    public List<EsGoods> findAllByFrontRelation(String relationKey, Integer page, Integer pageSize) {
        var results = new ArrayList<EsGoods>();
        var qb = QueryBuilders.termQuery("frontRelations.relationKey", relationKey);
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(qb);
        sourceBuilder.from((page - 1) * pageSize);
        sourceBuilder.size(pageSize);
        try {
            var searchResponse = /* highLevelUtil.getClient() */client.search(new SearchRequest(new String[]{TuxianIndexEnum.INDEX_GOODS.getIndexName()}, sourceBuilder), RequestOptions.DEFAULT);
            Stream.of(searchResponse.status().getStatus()).filter(status -> status == HttpStatus.HTTP_OK)
                    .forEach(status ->
                            Optional.ofNullable(searchResponse.getHits().getHits())
                                    .filter(hits -> hits.length > 0)
                                    .ifPresent(hits ->
                                            Arrays.stream(hits).forEach(hit -> results.add(JSONUtil.toBean(hit.getSourceAsString(), EsGoods.class)))
                                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 根据发布时间和最大条目数查询es中的商品数据
     * @param page       当前页数
     * @param pageSize   每页大小
     * @param sortMethod 排序方法：asc为正序、desc为倒序
     * @param isPre       是否为预售
     * @return
     */
    @Override
    public List<EsGoods> findAllGoodsWithPublishTime(Integer page,Integer pageSize, String sortMethod, Boolean isPre) {
        var searchRequest = new SearchRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName());
        var sourceBuilder = new SearchSourceBuilder();
        var rqb = QueryBuilders.rangeQuery(FIELD_PUBLISH_TIME);
        Stream.of(isPre).filter(Boolean.TRUE::equals).forEach(is -> rqb.gte(System.currentTimeMillis() / 1000));
        Stream.of(isPre).filter(Boolean.FALSE::equals).forEach(is -> rqb.lte(System.currentTimeMillis() / 1000));
        sourceBuilder.query(rqb);
        sourceBuilder.from((page - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.sort(new FieldSortBuilder(FIELD_PUBLISH_TIME).order(sortMethod.equals("asc") ? SortOrder.ASC : SortOrder.DESC));
        searchRequest.source(sourceBuilder);
        var results = new ArrayList<EsGoods>();
        try {
            SearchResponse searchResponse = /* highLevelUtil.getClient() */client.search(searchRequest, RequestOptions.DEFAULT);
            Optional.of(searchResponse.status().getStatus())
                    .filter(restStatus -> restStatus == HttpStatus.HTTP_OK)
                    .ifPresent(restStatus ->
                            results.addAll(Arrays
                                    .stream(searchResponse.getHits().getHits())
                                    .map(hit -> JSONUtil.toBean(hit.getSourceAsString(), EsGoods.class))
                                    .collect(Collectors.toList()))
                    );
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new BusinessException(ErrorMessageEnum.ES_SEARCHING_FAIL);
        }
        return results;
    }

    /**
     * 根据条件获取商品列表
     * @param queryVo
     * @return
     */
    // @Cacheable(value = "xiaotuxian",key = "methodName +'-queryVo:'+#root.args[0]")
    @Override
    public List<EsGoods> findAllGoods(GoodsQueryPageVo queryVo) {
        var searchRequest = new SearchRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName());
        var sourceBuilder = new SearchSourceBuilder();
        var bqb = QueryBuilders.boolQuery();
        bqb.must(QueryBuilders.rangeQuery(FIELD_PUBLISH_TIME)
                    .lte(System.currentTimeMillis() / 1000));
        if (CollUtil.isNotEmpty(queryVo.getIds())) {
            bqb.must(QueryBuilders.termsQuery("id", queryVo.getIds()));
        }
        if (CollUtil.isNotEmpty(queryVo.getDisIds())) {
            bqb.mustNot(QueryBuilders.termsQuery("id", queryVo.getDisIds()));
        }
        if (StrUtil.isNotEmpty(queryVo.getFrontId())) {
            bqb.must(QueryBuilders.termQuery(FIELD_FRONT_ID, queryVo.getFrontId()));
        }
        sourceBuilder.query(bqb);
        // if (CollUtil.isEmpty(queryVo.getIds())) {
        //     var from = (queryVo.getPage() - 1) * queryVo.getPageSize();
        //     sourceBuilder.from(from);
        //     sourceBuilder.size(queryVo.getPageSize());
        // }
        var from = (queryVo.getPage() - 1) * queryVo.getPageSize();
        sourceBuilder.from(from);
        sourceBuilder.size(queryVo.getPageSize());


        sourceBuilder.sort(new FieldSortBuilder(FIELD_PUBLISH_TIME).order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);
        var results = new ArrayList<EsGoods>();
        try {
            SearchResponse searchResponse = /* highLevelUtil.getClient() */client.search(searchRequest, RequestOptions.DEFAULT);
            Optional.of(searchResponse.status().getStatus())
                    .filter(restStatus -> restStatus == HttpStatus.HTTP_OK)
                    .ifPresent(restStatus ->
                            results.addAll(Arrays
                                    .stream(searchResponse.getHits().getHits())
                                    .map(hit -> JSONUtil.toBean(hit.getSourceAsString(), EsGoods.class))
                                    .collect(Collectors.toList()))
                    );
        } catch (IOException e) {
            throw new BusinessException(ErrorMessageEnum.ES_SEARCHING_FAIL);
        }
        return results;
    }

    @Override
    public SearchGoodsServiceVo search(SearchQueryVo queryVo) {
        AtomicReference<SearchGoodsServiceVo> ar = new AtomicReference<>();
        var searchResponse = getSearchResponse(queryVo, true);
        Optional.of(searchResponse.status().getStatus())
                .filter(restStatus -> restStatus == HttpStatus.HTTP_OK)
                .ifPresent(restStatus -> {
                    var vo = new SearchGoodsServiceVo();
                    var conditionVo = new SearchConditionVo();
                    //解析数据
                    Optional.ofNullable(searchResponse.getAggregations()).ifPresent(aggregations -> {
                        Optional.ofNullable(aggregations.get("by_category")).ifPresent(aggregation -> {
                            var categoryLayer = new AtomicInteger(1);
                            Optional.ofNullable(frontService.getById(queryVo.getCategoryId())).ifPresent(front -> categoryLayer.set(front.getLayer() + 1));
                            var categories = new ArrayList<FrontSimpleVo>();
                            var byCategoryAggregation = (Terms) aggregation;
                            byCategoryAggregation.getBuckets().forEach(bucket -> Optional.ofNullable(frontService.getById(bucket.getKeyAsString())).filter(front -> front.getLayer() == categoryLayer.get())
                                    .ifPresent(front -> categories.add(BeanUtil.toBean(front, FrontSimpleVo.class))));
                            conditionVo.setCategories(categories);
                        });
                        Optional.ofNullable(aggregations.get("by_brand")).ifPresent(aggregation -> {
                            var brands = new ArrayList<BrandSimpleVo>();
                            var byBrandAggregation = (Terms) aggregation;
                            byBrandAggregation.getBuckets().forEach(bucket -> Optional.ofNullable(brandService.getById(bucket.getKeyAsString()))
                                    .ifPresent(brand -> {
                                        var result = BeanUtil.toBean(brand, BrandSimpleVo.class);
                                        Optional.ofNullable(brand.getLogoId()).filter(StrUtil::isNotEmpty).flatMap(logoId ->
                                                Optional.ofNullable(pictureService.findById(logoId))).ifPresent(picture -> {
                                            result.setPicture(picture.getUrl());
                                            result.setLogo(picture.getUrl());
                                        });
                                        brands.add(result);
                                    }));
                            conditionVo.setBrands(brands);
                        });
                    });
                    vo.setConditions(conditionVo);
                    vo.setPageData(getPageData(searchResponse, queryVo.getPage(), queryVo.getPageSize()));
                    ar.set(vo);
                });
        return ar.get();
    }

    /**
     * @description: 按照分页从es查询商品
     * @param {SearchQueryVo} queryVo
     * @return {*}
     * @author: lbc
     */
    @Override
    public Page<EsGoods> searchByPage(SearchQueryVo queryVo) {
        log.info("从es中查询数据searchByPage：queryVo"+JSONObject.toJSONString(queryVo));

        var response = getSearchResponse(queryVo, false);
        return getPageData(response, queryVo.getPage(), queryVo.getPageSize());
    }

    @Override
    public void updateGoodOrderNum(String spuId, Integer num) {
        var request = new GetRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName(), spuId);
        try {
            var suc = /* highLevelUtil.getClient() */client.exists(request, RequestOptions.DEFAULT);
            if (suc) {
                var updateRequest = new UpdateRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName(), spuId).doc("orderNum", num);
                /* highLevelUtil.getClient() */client.update(updateRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException ioException) {
            log.info("解析ES返回的结果出错了");
        }
    }
    /**
     * 获取推荐商品
     */
    @Override
    public List<EsGoods> getHotGoods(HotGoodsQueryVo queryVo) {
        var spuIds = recordOrderSpuService.getHotGoods(queryVo).stream().map(HotGoodsVo::getSpuId).collect(Collectors.toList());
        var searchQueryVo = new GoodsQueryPageVo();
        searchQueryVo.setIds(spuIds);
        searchQueryVo.setPage(queryVo.getPage());
        searchQueryVo.setPageSize(queryVo.getPageSize());
        if(null != queryVo.getLimit()){
            searchQueryVo.setPageSize(queryVo.getLimit());
            queryVo.setPageSize(queryVo.getLimit());
        }
        var hotGoods = this.findAllGoods(searchQueryVo);
        if (hotGoods.size() < queryVo.getLimit()) {
            Optional.ofNullable(queryVo.getSpuId()).ifPresent(spuId -> spuIds.remove(queryVo.getSpuId()));
            searchQueryVo.setIds(null);
            searchQueryVo.setPageSize(queryVo.getPageSize() - hotGoods.size());
            searchQueryVo.setPage(queryVo.getPage());
            //？？？？
            searchQueryVo.setDisIds(spuIds);
            var additional = this.findAllGoods(searchQueryVo);
            hotGoods.addAll(additional);
        }
        return hotGoods;
    }


    /**
     * 获取es前台分类数据集合
     *
     * @param front 前台分类
     * @return es前台分类数据集合
     */
    private List<EsFront> getEsFronts(ClassificationFront front) {
        var results = new ArrayList<EsFront>();
        results.add(BeanUtil.toBean(front, EsFront.class));
        Optional.ofNullable(front.getPid()).filter(StrUtil::isNotEmpty).flatMap(pid ->
                Optional.ofNullable(frontService.getById(pid))).ifPresent(parent -> {
            results.add(BeanUtil.toBean(parent, EsFront.class));
            Optional.ofNullable(parent.getPid())
                    .filter(StrUtil::isNotEmpty).flatMap(ppid ->
                    Optional.ofNullable(frontService.getById(ppid))).ifPresent(pparent ->
                    results.add(BeanUtil.toBean(pparent, EsFront.class)));
        });
        return results;
    }

    /**
     * 解析搜索结果包装为分页数据
     *
     * @param response 搜索结果
     * @param page     页码
     * @param pageSize 页尺寸
     * @return 分页数据
     */
    private Page<EsGoods> getPageData(SearchResponse response, Integer page, Integer pageSize) {
        var ar = new AtomicReference<Page<EsGoods>>();
        Optional.ofNullable(response.getHits()).ifPresent(searchHits -> {
            //填充分页信息
            var total = searchHits.getTotalHits().value;
            var dataPage = new Page<EsGoods>(page, pageSize, total);
            //填充数据信息
            var datas = new ArrayList<EsGoods>();
            Optional.ofNullable(searchHits.getHits()).ifPresent(hits ->
                    Arrays.stream(hits).forEach(hit -> datas.add(JSONUtil.toBean(hit.getSourceAsString(), EsGoods.class))));
            dataPage.setRecords(datas);
            log.info("从es中查询出数据：datas"+datas.size());
            ar.set(dataPage);
        });
        return ar.get();
    }

    /**
     * 构建商品搜索请求
     *
     * @param queryVo       搜索条件
     * @param needCondition 是否需要返回筛选条件
     * @return 请求结果
     */
    private SearchResponse getSearchResponse(SearchQueryVo queryVo, @NotNull Boolean needCondition) {
        var searchRequest = new SearchRequest(TuxianIndexEnum.INDEX_GOODS.getIndexName());
        var sourceBuilder = new SearchSourceBuilder();
        var bqb = QueryBuilders.boolQuery();
        // 联想词Id集合
        if (CollUtil.isNotEmpty(queryVo.getAssociatedIds())) {
            bqb.should(QueryBuilders.termsQuery("hotKeys", queryVo.getAssociatedIds()));
        }
        // 分类id
        if (StrUtil.isNotEmpty(queryVo.getCategoryId())) {
            bqb.must(QueryBuilders.termQuery(FIELD_FRONT_ID, queryVo.getCategoryId()));
        }
        //前台分类frontIds
        if(CollUtil.isNotEmpty(queryVo.getFrontIds())){
            bqb.must(QueryBuilders.termsQuery(FIELD_FRONT_ID,queryVo.getFrontIds()));
        }
        // 品牌id
        if (StrUtil.isNotEmpty(queryVo.getBrandId())) {
            bqb.must(QueryBuilders.termQuery("brand.id", queryVo.getBrandId()));
        }
        // 只显示特惠
        Optional.ofNullable(queryVo.getOnlyDiscount()).filter(Boolean.TRUE::equals).ifPresent(only -> bqb.must(QueryBuilders.termQuery("hasDiscount", only)));
        // 最低价格
        if (queryVo.getLowPrice() != null) {
            bqb.must(QueryBuilders.rangeQuery("price").gte(queryVo.getLowPrice()));
        }
        // 最高价格
        if (queryVo.getHighPrice() != null) {
            bqb.must(QueryBuilders.rangeQuery("price").lte(queryVo.getHighPrice()));
        }
        // 是否有库存
        Stream.of(queryVo.getInventory()).filter(Objects::nonNull).filter(Boolean.TRUE::equals).forEach(has ->
                bqb.must(QueryBuilders.rangeQuery("inventory").gt(0)));
        // 属性条件
        if (CollUtil.isNotEmpty(queryVo.getAttrs())) {
            queryVo.getAttrs().forEach(searchPropertyVo ->
                    Stream.of(searchPropertyVo)
                            .filter(vo -> StrUtil.isNotEmpty(vo.getGroupName()) && StrUtil.isNotEmpty(vo.getPropertyName()))
                            .forEach(vo -> {
                                var subBqb = QueryBuilders.boolQuery();
//                                subBqb.must(QueryBuilders.termQuery("saleProperties.propertyGroupName", vo.getGroupName()));
//                                subBqb.must(QueryBuilders.termQuery("saleProperties.propertyMainName", vo.getGroupName()));
                                //2023-04-27 前台传值对应对应错误
                                //2023-05-18 将 bqb.should(subBqb);改成bqb.must，subBqb.must改成 subBqb.should
                                subBqb.should(QueryBuilders.termQuery("saleProperties.propertyMainName", vo.getGroupName()));
                                subBqb.should(QueryBuilders.termQuery("saleProperties.propertyValueName", vo.getPropertyName()));
                                // bqb.should(subBqb);
                                bqb.must(subBqb);
                            }));
        }
        // 所输入的关键词
        if (StrUtil.isNotEmpty(queryVo.getKeyword())) {
            String[] fields = {
                    "name", "backend.name", "brand.name", "brand.nameEn", "brand.productionPlaceCountry", "brand.productionPlaceState", "fronts.name"
                    , "pcDecription", "appDecription"
            };
            bqb.should(QueryBuilders.multiMatchQuery(queryVo.getKeyword(), fields).analyzer("ik_smart"));
        }
        sourceBuilder.query(bqb);
        Stream.of(needCondition).filter(Objects::nonNull).filter(Boolean.TRUE::equals).forEach(need -> {
            //聚合信息
            if (StrUtil.isNotEmpty(queryVo.getCategoryId())) {
                 sourceBuilder.aggregation(AggregationBuilders.terms("by_category").field(FIELD_FRONT_ID/*+".keyword"*/).size(1000));
            }
            sourceBuilder.aggregation(AggregationBuilders.terms("by_brand").field("brand.id"/*+".keyword"*/).size(100));
        });
        //请求分页信息
        sourceBuilder.from((queryVo.getPage() - 1) * queryVo.getPageSize());
        sourceBuilder.size(queryVo.getPageSize());
        // 排序
        if (StrUtil.isNotEmpty(queryVo.getSortField())) {
             // 增加排序的默认值desc
            if (StringUtils.isBlank(queryVo.getSortMethod())) {
                queryVo.setSortMethod(SortOrder.DESC.toString());
            }
            sourceBuilder.sort(new FieldSortBuilder(queryVo.getSortField()).order(SortOrder.fromString(queryVo.getSortMethod())));
        }
        searchRequest.source(sourceBuilder);
        try {
            return /* highLevelUtil.getClient() */client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException(ErrorMessageEnum.ES_SEARCHING_FAIL);
        }
    }
}
