package com.outsourcingdelivery.common.auth;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.user.enums.UserType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class UserTypeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 어노테이션 방식으로 수정
        if (handler instanceof HandlerMethod handlerMethod) {
            OwnerOnly ownerOnlyAnno = handlerMethod.getMethodAnnotation(OwnerOnly.class);
            UserOnly userOnlyAnno = handlerMethod.getMethodAnnotation(UserOnly.class);

            if (request.getAttribute("userType") != null) {     // 비회원인 경우엔 검사 안하고 바로 넘어감
                UserType userType = UserType.of((String) request.getAttribute("userType"));

                if (ownerOnlyAnno != null && !userType.equals(UserType.OWNER)) {
                    throw new ApplicationException(ErrorCode.FORBIDDEN_OWNER_ONLY);
                }

                if (userOnlyAnno != null && !userType.equals(UserType.USER)) {
                    throw new ApplicationException(ErrorCode.FORBIDDEN_USER_ONLY);
                }
            }

        }

        return true;
    }

}