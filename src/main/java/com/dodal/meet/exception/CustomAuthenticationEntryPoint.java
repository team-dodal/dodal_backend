package com.dodal.meet.exception;

import com.dodal.meet.controller.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String ex = (String) request.getAttribute("exception");
        response.setContentType("application/json;charset=utf8");
        if (!StringUtils.hasLength(ex) || ex.equals(ErrorCode.INVALID_TOKEN.name())) {
            response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(Response.builder().resultCode(ErrorCode.INVALID_TOKEN.name())
                    .result(ErrorCode.INVALID_TOKEN.getMessage()).build()));
            return;
        }

        if (ex.equals(ErrorCode.EXPIRED_TOKEN.name())) {
            response.setStatus(ErrorCode.EXPIRED_TOKEN.getStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(Response.builder().resultCode(ErrorCode.EXPIRED_TOKEN.name())
                    .result(ErrorCode.EXPIRED_TOKEN.getMessage()).build()));
        }
    }
}
