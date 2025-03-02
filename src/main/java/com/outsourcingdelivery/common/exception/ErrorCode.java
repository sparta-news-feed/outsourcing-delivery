package com.outsourcingdelivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL(CONFLICT, "이미 가입되어있는 이메일 입니다."),
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INCORRECT_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    MISSING_TOKEN(BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "RefreshToken이 만료되었습니다."),
    NOT_FOUND_TOKEN(BAD_REQUEST, "해당 토큰을 찾을 수 없습니다."),
    FORBIDDEN_OWNER_ONLY(FORBIDDEN, "가게 사장님만 접근할 수 있습니다."),
    USER_NOT_FOUND(BAD_REQUEST, "가입되지 않은 사용자입니다."),
    USER_ADDRESS_NOT_FOUND(BAD_REQUEST, "아이디에 해당하는 유저 주소가 없습니다."),
    INVALID_AUTH_ANNOTATION_USAGE(BAD_REQUEST, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(BAD_REQUEST, "유효하지 않는 JWT 토큰입니다."),
    ALREADY_DELETED_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다."),
    DELETED_USER_CANNOT_REGISTER(HttpStatus.BAD_REQUEST, "탈퇴한 사용자는 다시 가입할 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
