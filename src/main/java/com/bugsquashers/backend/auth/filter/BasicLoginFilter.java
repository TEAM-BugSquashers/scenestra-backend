package com.bugsquashers.backend.auth.filter;


import com.bugsquashers.backend.auth.jwt.JwtService;
import com.bugsquashers.backend.user.UserPrincipal;
import com.bugsquashers.backend.util.response.ApiResponse;
import com.bugsquashers.backend.util.response.ErrorStatus;
import com.bugsquashers.backend.util.response.SuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class BasicLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtService jwtService;

    public BasicLoginFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        //로그인 진행 url지정
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // id, pw를 요청에서 가져와 토큰화 후 getAuthenticationManager에게 인증 요청
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            log.info("로그인 시도됨: {}", loginRequest.username);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info(">> ID/PW 로그인 성공! UserID: {}, REQ IP: {}", authResult.getName(), request.getRemoteAddr());
        //jwt 토큰 발행 (쿠키로 설정됨)
        jwtService.createAccessToken(response, (UserPrincipal) authResult.getPrincipal());
        jwtService.createRefreshToken(response, (UserPrincipal) authResult.getPrincipal());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(SuccessStatus.OK.getHttpStatus().value());

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                SuccessStatus.OK.getCode(),
                SuccessStatus.OK.getMessage(),
                null
        );

        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패: {}", failed.getMessage());

        ErrorStatus errorStatus = ErrorStatus.UNAUTHORIZED;
        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String erroorMessage = failed.getMessage();
        if (failed instanceof BadCredentialsException) {
            erroorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        }
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                false,
                errorStatus.getCode(),
                erroorMessage,
                null
        );

        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }

    private record LoginRequest(
            String username,
            String password
    ) {
    }
}
