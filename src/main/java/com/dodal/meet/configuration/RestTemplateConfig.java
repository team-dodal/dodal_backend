package com.dodal.meet.configuration;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private final int CONN_TIMEOUT = 10 * 1000;
    private final int CONN_REQ_TIMEOUT = 10 * 1000;
    private final int READ_TIMEOUT = 10 * 1000;

    @Bean
    public RestTemplate restTemplate() {
        // org.apache.http.client.HttpClient 생성 후 Connection Pool 설정
        HttpClient client = HttpClientBuilder
                .create()
                .setMaxConnTotal(Runtime.getRuntime().availableProcessors()) // 최대 오픈 커넥션 수 제한
                .setMaxConnPerRoute(Runtime.getRuntime().availableProcessors()) // 호스트(IP, PORT 조합)에 대한 커넥션 수 제한
                .build();

        // ClientHttpRequestFactory의 구현체인 HttpComponentsClientHttpRequestFactory 생성
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client); // HttpClient를 직접 설정하지 않는 경우 생략해도 된다.
        factory.setConnectTimeout(CONN_TIMEOUT); // 커넥션 타임아웃 설정
        factory.setConnectionRequestTimeout(CONN_REQ_TIMEOUT); // 요청 커넥션 타임아웃 설정
        factory.setReadTimeout(READ_TIMEOUT);

        return new RestTemplate(factory);
    }
}
