package com.outsourcingdelivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken 이 만료되었습니다."),
    NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "해당 토큰을 찾을 수 없습니다."),
    FORBIDDEN_OWNER_ONLY(HttpStatus.FORBIDDEN, "가게 사장님만 접근할 수 있습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "가입되지 않은 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
