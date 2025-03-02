package com.outsourcingdelivery.common.auth;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
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
import java.io.PrintWriter;
import java.time.LocalDateTime;
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
            "/api/v1/auth/login",
            "/api/v1/auth/refresh"
        }
    );

    private final JwtUtil jwtUtil;

    // 필터에서는 토큰의 검증만, Type 검증은 인터셉터에서
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

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
            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims.isEmpty()) {
                    httpResponse.sendError(SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                    return;
                }

                httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
                httpRequest.setAttribute("userType", claims.get("userType"));

                chain.doFilter(request, response);
            } catch (SecurityException | MalformedJwtException ex) {
                throw new ApplicationException(ErrorCode.INVALID_JWT_SIGNATURE);
            } catch (ExpiredJwtException ex) {
                throw new ApplicationException(ErrorCode.EXPIRED_JWT_TOKEN);
            } catch (Exception ex) {
                throw new ApplicationException(ErrorCode.INVALID_JWT_TOKEN);
            }
        } catch (ApplicationException ex) {
            parseResponseErrorMessage(httpResponse, ex);
        }
    }

    private void parseResponseErrorMessage(HttpServletResponse httpResponse, ApplicationException ex) throws IOException {
        httpResponse.setStatus(ex.getStatus().value());
        httpResponse.setContentType("application/json;charset=UTF-8");

        String errorBody = String.format("""
                    {
                        "status": "%s",
                        "code": "%d",
                        "message": "%s",
                        "timestamp": "%s"
                    }
                    """, ex.getStatus().name(), ex.getStatus().value(), ex.getMessage(), LocalDateTime.now());

        PrintWriter writer = httpResponse.getWriter();
        writer.println(errorBody);
        writer.flush();
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
