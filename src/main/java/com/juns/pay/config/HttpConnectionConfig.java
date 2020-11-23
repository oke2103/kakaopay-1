package com.juns.pay.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class HttpConnectionConfig {

    @Bean
    public RestTemplate getCustomRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(2000); // 읽기시간초과, ms
        httpRequestFactory.setReadTimeout(3000); // 연결시간초과, ms
        CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(200)  // connection pool 적용
            .setMaxConnPerRoute(20)  // connection pool 적용
            .build();
        httpRequestFactory.setHttpClient(httpClient);
        return new RestTemplate(httpRequestFactory);
    }
}

