package com.itheima.xiaotuxian.service.search.impl;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.enums.TuxianIndexEnum;
import com.itheima.xiaotuxian.entity.search.EsKeyword;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.search.SearchKeywordService;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;

@Service
public class SearchKeywordServiceImpl implements SearchKeywordService {
    // @Autowired
    // private HighLevelUtil highLevelUtil;
    @Autowired
    private RestHighLevelClient client;
    @Override
    public Boolean saveKeyword(EsKeyword esKeyword) {
        Optional.ofNullable(esKeyword).map(EsKeyword::getId).ifPresent(id -> {
            var request = new IndexRequest(TuxianIndexEnum.INDEX_KEYWORD.getIndexName());
            request.id(id);
            request.source(JSONUtil.toJsonStr(esKeyword), XContentType.JSON);
            try {
                /* highLevelUtil.getClient() */client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.ES_INSERT_FAILED);
            }
        });
        return true;
    }

    @Override
    public Boolean deleteKeyword(String id) {
        if (StrUtil.isNotEmpty(id)) {
            DeleteRequest request = new DeleteRequest(TuxianIndexEnum.INDEX_KEYWORD.getIndexName(), id);
            try {
                /* highLevelUtil.getClient() */client.delete(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.ES_DELETE_FAILED);
            }
        }
        return true;
    }

    @Override
    public List<EsKeyword> findAll(String keyword) {
        var searchSourceBuilder = new SearchSourceBuilder();
        var searchRequest = new SearchRequest(TuxianIndexEnum.INDEX_KEYWORD.getIndexName());
        var sbd = SuggestBuilders.completionSuggestion("associatedWords").prefix(keyword).size(20);
        var suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("searchTips", sbd);
        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);
        var results = new ArrayList<EsKeyword>();
        try {
            SearchResponse searchResponse = /* highLevelUtil.getClient() */client.search(searchRequest, RequestOptions.DEFAULT);
            Optional.ofNullable(searchResponse.status().getStatus())
                    .filter(restStatus -> restStatus == HttpStatus.HTTP_OK)
                    .ifPresent(restStatus -> {
                                var suggest = searchResponse.getSuggest();
                                var suggestion = suggest.getSuggestion("searchTips");
                                suggestion.getEntries().stream().forEach(entry -> {
                                    CompletionSuggestion.Entry en = (CompletionSuggestion.Entry) entry;
                                    for (CompletionSuggestion.Entry.Option option : en) {
                                        results.add(JSONUtil.toBean(option.getHit().getSourceAsString(), EsKeyword.class));
                                    }
                                });
                            }
                    );
        } catch (IOException e) {
            throw new BusinessException(ErrorMessageEnum.ES_SEARCHING_FAIL);
        }
        return results;
    }
}
