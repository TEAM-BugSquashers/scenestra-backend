package com.bugsquashers.backend.auth.jwt;


import com.bugsquashers.backend.user.service.CustomUserDetailsService;
import com.bugsquashers.backend.user.UserPrincipal;
import com.bugsquashers.backend.user.service.UserService;
import com.bugsquashers.backend.user.domain.RefreshToken;
import com.bugsquashers.backend.user.domain.User;
import com.bugsquashers.backend.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bugsquashers.backend.auth.jwt.JwtRule.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {
    private final JwtGenerator jwtGenerator;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public String createAccessToken(HttpServletResponse response, User user) {
        String accessToken = jwtGenerator.generateAccessToken(user);
        ResponseCookie cookie = jwtGenerator.createTokenCookie(ACCESS_PREFIX.getValue(), accessToken);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        log.info("Access Token 발급 성공");
        return accessToken;
    }

    public String createAccessToken(HttpServletResponse response, UserPrincipal userPrincipal) {
        User user = userService.getUserById(userPrincipal.getUserId());
        return createAccessToken(response, user);
    }

    @Transactional
    public String createRefreshToken(HttpServletResponse response, User user) {
        String refreshToken = jwtGenerator.generateRefreshToken(user);
        ResponseCookie cookie = jwtGenerator.createTokenCookie(REFRESH_PREFIX.getValue(), refreshToken);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(new RefreshToken(user, refreshToken, jwtGenerator.getRefreshTokenExpTime()));
        log.info("Refresh Token 발급 및 저장 성공");
        return refreshToken;
    }

    @Transactional
    public String createRefreshToken(HttpServletResponse response, UserPrincipal userPrincipal) {
        User user = userService.getUserById(userPrincipal.getUserId());
        return createRefreshToken(response, user);
    }

    public boolean validateAccessToken(String token) {
        return getTokenStatus(token, ACCESS_PREFIX) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token) {
        return getTokenStatus(token, REFRESH_PREFIX) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshTokenWithUser(String token, User user) {
        RefreshToken storedToken = refreshTokenRepository.findTopByUserOrderByIdDesc(user).orElseThrow();
        return storedToken.getToken().equals(token);

    }


    private TokenStatus getTokenStatus(String token, JwtRule jwtRule) {
        try {
            SecretKey key = jwtRule == ACCESS_PREFIX ? jwtGenerator.getAccessKey() : jwtGenerator.getRefreshKey();

            Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token);
            log.debug("토큰검증:성공");
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException e) {
            log.debug("토큰검증:만료");
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            log.debug("토큰검증:에러");
            return TokenStatus.INVALID;
        }
    }

    public String getUsernameFromAccessToken(String token) {
        try {
            return Jwts.parser().verifyWith(jwtGenerator.getAccessKey()).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

    }

    public String getUsernameFromRefreshToken(String token) {
        try {
            return Jwts.parser().verifyWith(jwtGenerator.getRefreshKey()).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

    }

    public User getUserFromRefreshToken(String token) {
        return userService.getUserByUsername(getUsernameFromRefreshToken(token));
    }

    public Authentication getAuthentication(String token) {
        try {
            // 토큰에서 클레임 정보 추출
            var claims = Jwts.parser().verifyWith(jwtGenerator.getAccessKey()).build()
                    .parseSignedClaims(token).getPayload();
            
            String username = claims.getSubject();
            Long userId = claims.get("Identifier", Long.class);
            Boolean isAdmin = claims.get("isAdmin", Boolean.class);
            
            // 토큰 정보만으로 UserPrincipal 생성
            UserPrincipal principal = new UserPrincipal(userId, username);
            
            // isAdmin 값에 따라 권한 설정
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            if (isAdmin != null && isAdmin) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("인증 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
            throw new AuthenticationException("인증 처리 중 오류가 발생했습니다", e) {};
        }
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void deleteTokenFromCookie(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_PREFIX.getValue(), null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_PREFIX.getValue(), null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .build();

        response.addHeader(JWT_RESOLVE_HEADER.getValue(), accessTokenCookie.toString());
        response.addHeader(JWT_RESOLVE_HEADER.getValue(), refreshTokenCookie.toString());
    }

    @Transactional
    public void blackRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("[JWT Service] 유저의 RefreshToken 무효화 처리됨: " + user.getUsername());
    }

    @Transactional
    public void blackRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
        log.info("[JWT Service] 해당되는 RefreshToken 무효화 처리!");
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);

        deleteTokenFromCookie(response);
        if(refreshToken != null) {
            User user = getUserFromRefreshToken(refreshToken);
            log.info(user.getUsername());
            blackRefreshToken(user);
        }
    }


}
