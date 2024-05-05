package com.itheima.xiaotuxian.controller.goods;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.KeywordStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.goods.GoodsKeyword;
import com.itheima.xiaotuxian.entity.goods.GoodsSpu;
import com.itheima.xiaotuxian.entity.search.EsGoods;
import com.itheima.xiaotuxian.service.goods.GoodsKeywordService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.mq.producer.GoodsProducer;
import com.itheima.xiaotuxian.service.mq.producer.KeywordProducer;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.service.search.SearchKeywordService;
import com.itheima.xiaotuxian.vo.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

/*
 * @author: lbc
 * @Date: 2023-04-29 18:06:00
 * @Descripttion: 
 */
@RestController
@Slf4j
@RequestMapping("/goodsSpider")
public class GoodsSpiderEsController extends BaseController {
    @Autowired
    private GoodsProducer goodsProducer;
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private SearchGoodsService searchGoodsService;


    @Autowired
    private GoodsKeywordService goodsKeywordService;
    @Autowired
    private KeywordProducer keywordProducer;
    @Autowired
    private SearchKeywordService searchKeywordService;
    
    /**
     * 爬虫数据同步到数据库后，自动将数据导入es中
     *
     */
    @GetMapping("/saveSpiderGoods")
    public R<String> saveSpiderGoods(){
        List<GoodsSpu> spuList = goodsSpuService.list();
        List<String> spuIds = new ArrayList<>();
        Optional.ofNullable(spuList).filter(CollUtil::isNotEmpty).ifPresent(
                goodsSpus -> {
                    goodsSpus.stream().forEach(spu->{
                        goodsProducer.sendOperator(spu.getId(), GoodsStatic.OP_TYPE_AUDIT);
                        log.info("saveSpiderGoods：spuId{},name:{}", spu.getId(),spu.getName());
                        spuIds.add(spu.getId());
                    });

                }
        );
        log.info("saveSpiderGoods结束：{}", JSON.toJSON(spuIds));

        return R.ok("saveSpiderGoods结束:spuList.size:"+spuList.size()+JSON.toJSON(spuIds));
    }

    @GetMapping("/deleteSpiderGoods")
    // @CacheEvict(value="xiaotuxian", allEntries=true)
    public R<String> deleteSpiderGoods(){
       
        var list = searchGoodsService.findAllGoodsWithPublishTime(1,1000, "desc", false)
                .stream().map(EsGoods::getId).collect(Collectors.toList());
        Optional.ofNullable(list).ifPresent(ids ->{
            ids.stream().forEach(id->searchGoodsService.deleteGoods(id));
        });

        return R.ok("deleteSpiderGoods结束：list.size:"+list.size()+JSON.toJSONString(list));
    }



    @GetMapping("/saveGoodsKeyWord")
    public R<String> saveGoodsKeyWord(){
        List<GoodsKeyword> goodsKeyWordList = goodsKeywordService.list();
        List<String> goodsKewWordIdsList = new ArrayList<>();
        Optional.ofNullable(goodsKeyWordList).filter(CollUtil::isNotEmpty).ifPresent(
                goodsKeyWords -> {
                    goodsKeyWords.stream().forEach(keyWord->{
                        keywordProducer.sendOperator(keyWord.getId(), KeywordStatic.OP_TYPE_SAVE);
                        goodsKewWordIdsList.add(keyWord.getId());
                    });

                }
        );
        log.info("saveSpiderGoods结束：{}", JSON.toJSON(goodsKewWordIdsList));
        return R.ok("saveSpiderGoods结束:spuList.size:"+goodsKeyWordList.size()+JSON.toJSON(goodsKewWordIdsList));
    }

    @GetMapping("/deleteGoodsKeyWord")
    public R<String> deleteGoodsKeyWord(){
          
        List<GoodsKeyword> goodsKeyWordList = goodsKeywordService.list();
        List<String> goodsKewWordIdsList = new ArrayList<>();
        Optional.ofNullable(goodsKeyWordList).ifPresent(keyWords ->{
            keyWords.stream().forEach(keyWord->{
                searchKeywordService.deleteKeyword(keyWord.getId());
                goodsKewWordIdsList.add(keyWord.getId());
            });
        });

        return R.ok("deleteSpiderGoods结束：list.size:"+goodsKewWordIdsList.size()+JSON.toJSONString(goodsKewWordIdsList));

    }

}
