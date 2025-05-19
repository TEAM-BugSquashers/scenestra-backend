package com.bugsquashers.backend.auth.filter;

import com.bugsquashers.backend.auth.jwt.JwtRule;
import com.bugsquashers.backend.auth.jwt.JwtService;
import com.bugsquashers.backend.user.domain.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    // 검사 제외할 URL 목록
    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/api/user/join",
            "/api/login",
            "/api/logout"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        boolean isApiRequest = requestURI.startsWith("/api");
        boolean isExceptionallyChecked = requestURI.equals("/vote");
        boolean isExcluded = EXCLUDED_URLS.contains(requestURI);

        // "/api/**" 하위라도 제외 리스트(EXCLUDED_URLS)에 포함되면 필터 패스
        if ((isApiRequest && isExcluded) || (!isApiRequest && !isExceptionallyChecked)) {
            log.debug("[JWT Filter] 예외 URL({}) 요청이므로 필터 패스", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.debug("[JWT Filter] 서비스 시작");

            String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
            if (accessToken != null) {
                log.debug("[JWT Filter] 액세스 토큰 검증 시작..");

                if (jwtService.validateAccessToken(accessToken)) {
                    log.debug("[JWT Filter] 액세스 토큰 검증 성공!");
                    setAuthentication(accessToken);
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    log.debug("[JWT Filter] 액세스 토큰이 유효하지 않거나 만료됨");
                }
            } else {
                log.debug("[JWT Filter] 쿠키에서 액세스 토큰을 발견할 수 없음");
            }

            String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);

            if (refreshToken != null) {
                log.debug("[JWT Filter] 리프레쉬 토큰 검증 시작..");
                try {
                    if (jwtService.validateRefreshToken(refreshToken)) {
                        User user = jwtService.getUserFromRefreshToken(refreshToken);
                        if (jwtService.validateRefreshTokenWithUser(refreshToken, user)) {
                            log.debug("[JWT Filter] 리프레쉬 토큰 검증 성공!");

                            // 리프레쉬 토큰 검증 성공 -> 액세스 재발급 및 인증처리
                            String newAccessToken = jwtService.createAccessToken(response, user);
                            jwtService.createRefreshToken(response, user);
                            setAuthentication(newAccessToken);
                            filterChain.doFilter(request, response);
                            return;
                        } else {
                            //리프레쉬 토큰 검증 실패 -> 로그아웃처리
                            log.debug("[JWT Filter] 리프레쉬 토큰이 일치하지 않음");
                            jwtService.blackRefreshToken(user);

                        }
                    } else {
                        log.debug("[JWT Filter] 리프레쉬 토큰이 유효하지 않거나 만료됨");
                        jwtService.blackRefreshToken(refreshToken);
                    }
                } catch (Exception e) {
                    log.debug("[JWT Filter] 리프레쉬 토큰 처리 중 오류 발생", e);

                }
            } else {
                log.debug("[JWT Filter] 쿠키에서 리프레쉬 토큰을 발견할 수 없음");
            }
            filterChain.doFilter(request, response);
        } catch (JwtException | AuthenticationException e) {
            log.error("[JWT Filter] 인증 처리 중 오류 발생: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
