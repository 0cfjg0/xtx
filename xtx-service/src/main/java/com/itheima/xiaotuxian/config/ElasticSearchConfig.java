package com.itheima.xiaotuxian.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/*
 * @author: lbc
 * @Date: 2023-05-12 11:32:02
 * @Descripttion:
 */
@Component
public class ElasticSearchConfig {
    @Value("${elasticsearch.host:127.0.0.1}")
    private String esHost;
    @Value("${elasticsearch.port:9200}")
    private Integer esPort;
    @Value("${elasticsearch.scheme:http}")
    private String scheme;

    @Value("${elasticsearch.connectTimeout:5000}")
    private Integer connectTimeout;
    @Value("${elasticsearch.socketTimeout:5000}")
    private Integer socketTimeout;
    @Value("${elasticsearch.connectionRequestTimeout:5000}")
    private Integer connectionRequestTimeout;

    @Value("${elasticsearch.maxConnectNum:100}")
    private Integer maxConnectNum;
    @Value("${elasticsearch.maxConnectPerRoute:100}")
    private Integer maxConnectPerRoute;
    // @Bean
    // public RestHighLevelClient restClient() {
    //     RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort)));
    //     return restClient;
    // }


    @Bean
    public RestClientBuilder restClientBuilder() {
        return RestClient.builder(makeHttpHost());
    }

    @Bean
    public RestClient restClient(){
        return RestClient.builder(new HttpHost(esHost, esPort, scheme)).build();
    }

    private HttpHost makeHttpHost() {
        return new HttpHost(esHost, esPort, scheme);
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder){
         // 异步httpclient连接延时配置
         restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            // HttpHost proxy = new HttpHost("127.0.0.1", 22, "http");
            // requestConfigBuilder.setProxy(proxy);
            return requestConfigBuilder;
        });

        // 异步httpclient配置
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            // httpclient连接数配置
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            // httpclient保活策略
            // httpClientBuilder.setKeepAliveStrategy(
            //         CustomConnectionKeepAliveStrategy.getInstance(esProperties.getKeepAliveMinutes()));
            return httpClientBuilder;
        });
        return new RestHighLevelClient(restClientBuilder);
    }
}
