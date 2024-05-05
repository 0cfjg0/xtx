package com.itheima.xiaotuxian.util;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HighLevelUtil {
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private Integer port;

    private RestHighLevelClient client;

    /**
     * 初始化client
     */
    public void init() {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }

    public RestHighLevelClient getClient() {
        if (client == null) {
            init();
        }
        return client;
    }

    public void close() throws IOException {
        client.close();
    }

}
