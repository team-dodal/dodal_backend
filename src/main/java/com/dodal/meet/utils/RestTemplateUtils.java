package com.dodal.meet.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtils {
    private static final int connTimeout = 10 * 1000;
    private static final int connReqTimeout = 10 * 1000;
    private static final int readTimeout = 10 * 1000;

    public static RestTemplate getRestTemplate() {
        // org.apache.http.client.HttpClient 생성 후 Connection Pool 설정
        HttpClient client = HttpClientBuilder
                .create()
                .setMaxConnTotal(Runtime.getRuntime().availableProcessors()) // 최대 오픈 커넥션 수 제한
                .setMaxConnPerRoute(Runtime.getRuntime().availableProcessors()) // 호스트(IP, PORT 조합)에 대한 커넥션 수 제한
                .build();

        // ClientHttpRequestFactory의 구현체인 HttpComponentsClientHttpRequestFactory 생성
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client); // HttpClient를 직접 설정하지 않는 경우 생략해도 된다.
        factory.setConnectTimeout(connTimeout); // 커넥션 타임아웃 설정
        factory.setConnectionRequestTimeout(connReqTimeout); // 요청 커넥션 타임아웃 설정
        factory.setReadTimeout(readTimeout);

        return new RestTemplate(factory);
    }
}
