package com.outsourcingdelivery.common.auth;

import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.user.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtFilterTest {

    private JwtFilter jwtFilter;
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    private static final String SECRET_KEY = "my-secret-key-for-testing-my-secret-key-for-testing";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jwtUtil = new JwtUtil(SECRET_KEY);
        jwtUtil.init();
        jwtFilter = new JwtFilter(jwtUtil);
    }

    @DisplayName("화이트리스트 경로 요청 시 필터 통과")
    @Test
    void isWhiteList() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/stores/123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @DisplayName("유효한 JWT 토큰이 있을 경우 정상적으로 필터 통과")
    @Test
    void validJwtPass() throws Exception {
        // given
        String accessToken = jwtUtil.createAccessToken(1L, UserType.OWNER);

        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/api/v1/users/password");
        request.addHeader("Authorization", accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(request.getAttribute("userId")).isEqualTo(1L);
        assertThat(request.getAttribute("userType")).isEqualTo("OWNER");
    }

    @DisplayName("AccessToken 생성 및 검증")
    @Test
    void createAndValidateAccessToken() throws Exception {
        // given
        Long userId = 1L;
        UserType owner = UserType.OWNER;

        // when
        String accessToken = jwtUtil.createAccessToken(userId, owner);
        String token = jwtUtil.substringToken(accessToken);
        Claims claims = jwtUtil.extractClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("userType")).isEqualTo(owner.name());
    }

    @DisplayName("RefreshToken 생성 및 검증")
    @Test
    void createAndValidateRefreshToken() throws Exception {
        // given
        Long userId = 1L;

        // when
        String refreshToken = jwtUtil.createRefreshToken(userId);
        Claims claims = jwtUtil.extractClaims(refreshToken);

        // then
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
    }

    @DisplayName("JWT 서명이 변조된 경우 INVALID_JWT_SIGNATURE 예외 발생")
    @Test
    void invalidTokenThrowsException() throws Exception {
        // given
        String invalidJwt = "invalid.jwt.signature";

        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/api/v1/users/password");
        request.addHeader("Authorization", "Bearer " + invalidJwt);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.INVALID_JWT_SIGNATURE.getHttpStatus().value());
        assertThat(response.getContentAsString()).contains(ErrorCode.INVALID_JWT_SIGNATURE.getMessage());
    }

    @DisplayName("JWT 만료 시 EXPIRED_JWT_TOKEN 예외 발생")
    @Test
    void expiredJwtToken() throws Exception {
        // given
        String expiredJwt = Jwts.builder()
            .subject("1")
            .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
            .expiration(Date.from(Instant.now().minusSeconds(3600)))
            .signWith(key)
            .compact();

        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/api/v1/users/password");
        request.addHeader("Authorization", "Bearer " + expiredJwt);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        assertThat(response.getStatus()).isEqualTo(ErrorCode.EXPIRED_JWT_TOKEN.getHttpStatus().value());
        assertThat(response.getContentAsString()).contains(ErrorCode.EXPIRED_JWT_TOKEN.getMessage());
    }

}