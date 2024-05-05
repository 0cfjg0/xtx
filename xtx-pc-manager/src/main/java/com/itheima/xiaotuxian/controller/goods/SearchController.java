package com.itheima.xiaotuxian.controller.goods;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.service.search.SearchKeywordService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.search.SearchGoodsVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;
import com.itheima.xiaotuxian.vo.search.SearchTipsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 搜索商品的控制类
 */
@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController extends BaseController {
    @Autowired
    private SearchKeywordService searchKeywordService;
    @Autowired
    private SearchGoodsService searchGoodsService;

    /**
     * 搜索-提示语
     *
     * @param keyword 关键词
     * @return 提示语
     */
    @GetMapping("/tips")
    public R<List<SearchTipsVo>> getTips(@RequestParam(name = "keyword") String keyword) {
        var keywords = searchKeywordService.findAll(keyword);
        var tipsMap = new HashMap<String, SearchTipsVo>();
        keywords.forEach(esKeyword -> Optional.ofNullable(esKeyword.getAssociatedWords()).filter(CollUtil::isNotEmpty)
                .ifPresent(associatedWords -> associatedWords.forEach(associatedWord ->
                        Optional.ofNullable(tipsMap.get(associatedWord)).ifPresentOrElse(tipsVo -> {
                            var ids = new ArrayList<String>(tipsVo.getIds());
                            ids.add(esKeyword.getId());
                            tipsVo.setIds(ids);
                        }, () -> {
                            var tipsVo = new SearchTipsVo();
                            tipsVo.setAssociatedWord(associatedWord);
                            tipsVo.setIds(Arrays.asList(esKeyword.getId()));
                            tipsMap.put(associatedWord, tipsVo);
                        }))));
        return R.ok(tipsMap.values().stream().collect(Collectors.toList()));
    }

    /**
     * 搜索-商品搜索-带筛选条件
     *
     * @param queryVo 搜索条件
     * @return 商品数据
     */
    @PostMapping("/all")
    public R<SearchGoodsVo> findAll(@RequestBody SearchQueryVo queryVo) {
        var serviceVo = searchGoodsService.search(queryVo);
        var result = BeanUtil.toBean(serviceVo, SearchGoodsVo.class);
        Optional.ofNullable(serviceVo.getPageData()).ifPresent(esGoodsPage ->
                result.setPageData(new Pager<>(esGoodsPage.getTotal()
                        , esGoodsPage.getSize()
                        , esGoodsPage.getPages()
                        , esGoodsPage.getCurrent(),
                        esGoodsPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())))
        );
        return R.ok(result);
    }
}
