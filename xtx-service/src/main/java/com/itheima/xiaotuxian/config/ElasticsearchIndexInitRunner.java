package com.itheima.xiaotuxian.config;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.enums.TuxianIndexEnum;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ElasticsearchIndexInitRunner implements ApplicationRunner {
    // @Autowired
    // private HighLevelUtil highLevelUtil;
    @Autowired
    private RestHighLevelClient client;
    @Override
    public void run(ApplicationArguments args) {
        ListUtil.toList(TuxianIndexEnum.values()).forEach(indexEnum ->
                Optional.of(indexExists(indexEnum.getIndexName()))
                        .filter(BooleanUtil::isFalse)
                        .ifPresent(exists -> {
                            try {
                                var classPathResource = new ClassPathResource(indexEnum.getMappingPath());
                                var result = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()))
                                        .lines().collect(Collectors.joining(System.lineSeparator()));
                                var request = new CreateIndexRequest(indexEnum.getIndexName());
                                request.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0));
                                request.mapping(result, XContentType.JSON);
                                Optional.ofNullable(/* highLevelUtil.getClient() */client.indices().create(request, RequestOptions.DEFAULT))
                                        .ifPresentOrElse(response -> log.info("init index => {}  SUCCESS", indexEnum.getIndexName()), this::afterException);
                            } catch (IOException e) {
                                log.error(e.getMessage(),e);
                                afterException();
                            }
                        })
        );
        log.info("ElasticsearchIndexInit init complete");
    }

    /**
     * 索引是否存在
     *
     * @param indexName 索引名称
     * @return 是否存在
     */
    private Boolean indexExists(String indexName) {
        try {
            var request = new GetIndexRequest(indexName);
            return /* highLevelUtil.getClient() */client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            afterException();
        }
        return false;
    }

    /**
     * 抛出异常后的处理
     */
    private void afterException() {
        log.error(ErrorMessageEnum.ES_INIT_FAILED.getMessage());
        System.exit(0);
    }
}
