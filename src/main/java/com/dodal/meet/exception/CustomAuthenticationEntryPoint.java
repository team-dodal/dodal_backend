package com.dodal.meet.exception;

import com.dodal.meet.controller.response.Response;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf8");
        response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value());
        response.getWriter().write(Response.builder().resultCode(ErrorCode.INVALID_TOKEN.name())
                .result(ErrorCode.INVALID_TOKEN.getMessage()).build().toStream());
    }
}
