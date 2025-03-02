package com.outsourcingdelivery.common.auth;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class UserTypeInterceptor implements HandlerInterceptor {

    private static final Map<String, String[]> WHITE_LIST = Map.of(
        "POST", new String[] {
           "/api/v1/stores/**"
        },
        "PATCH", new String[] {
            "/api/v1/stores/**"
        },
        "PUT", new String[] {
            "/api/v1/menus/**"
        },
        "DELETE", new String[] {
            "/api/v1/stores/**",
            "/api/v1/menus/**"
        }
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isWhiteList(request)) {
            String userType = String.valueOf(request.getAttribute("userType"));
            if (!"OWNER".equals(userType)) {
                throw new ApplicationException(ErrorCode.FORBIDDEN_OWNER_ONLY);
            }
        }

        return true;
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
