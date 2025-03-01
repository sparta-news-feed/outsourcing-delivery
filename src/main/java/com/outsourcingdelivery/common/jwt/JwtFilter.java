package com.outsourcingdelivery.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private static final Map<String, String[]> WHITE_LIST = Map.of(
        "GET", new String[]{
            "/api/v1/stores/**"
        },
        "POST", new String[] {
            "/api/v1/auth/signup",
            "/api/v1/auth/login"
        }
    );

    private final JwtUtil jwtUtil;

    // 필터에서는 토큰의 검증만, Type 검증은 인터셉터에서
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (isWhiteList(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String bearer = httpRequest.getHeader("Authorization");

        if (bearer == null || bearer.isEmpty()) {
            httpResponse.sendError(SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearer);

        try {
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims.isEmpty()) {
                httpResponse.sendError(SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute("email", claims.get("email"));
            httpRequest.setAttribute("userType", claims.get("userType"));

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않는 JWT 토큰입니다.");
        }
    }

    private boolean isWhiteList(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (!WHITE_LIST.containsKey(method)) {
            return false;
        }

        String[] lists = WHITE_LIST.get(method);
        return PatternMatchUtils.simpleMatch(lists, path);
    }
}
