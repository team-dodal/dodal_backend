package com.dodal.meet.configuration;


import com.dodal.meet.filter.JwtTokenFilter;
import com.dodal.meet.exception.CustomAuthenticationEntryPoint;
import com.dodal.meet.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity // 스프링 필터 체인에 시큐리티 등록
@Configuration
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String key;

    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        return http.csrf().disable().
                requestMatchers(matchers-> matchers.antMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger*/**",
                        "/api/**/users/sign-in", "/api/**/users/sign-up", "/api/**/users/nickname/**", "/api/**/users/profile",
                        "/api/**/categories/tags", "/image/**", "/favicon.ico"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .build();
    }

    /*
        csrf().disable() : CSRF 공격 방지 기능을 비활성화 csrf 서버에서 세션을 유지하고 있을 때 외부 사이트를 통해 세션 정보를 탈취해 악의적인 전송을 하는 것이나 JWT는 세션을 사용하지 않기 때문에 비활성화
        authorizeHttpRequests() : HTTP 요청에 대한 인가 설정
        antMatchers("/api/**").authenticated() : "/api/"로 시작하는 URL에 대한 요청은 인증을 필요
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) : 세션을 사용하지 않고, 각 요청마다 인증 정보를 전달 (JWT 인증 방식)
        addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class) : JwtTokenFilter를 UsernamePasswordAuthenticationFilter 앞에 추가하여 JWT 토큰 인증을 수행
        exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint()) : 인증에 실패한 요청에 대해 처리할 핸들러 등록
    */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests() // URL 별 권한 설정
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(new ObjectMapper()))
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        return http.build();
    }

}
