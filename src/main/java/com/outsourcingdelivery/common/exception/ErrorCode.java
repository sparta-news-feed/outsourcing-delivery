package com.outsourcingdelivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    MOCK(HttpStatus.BAD_REQUEST, "안녕하세요."),
    INVALID_USER_ENUM_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 UserType 입니다."),
    INVALID_STORE_STATUS_ENUM_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 StoreStatus 입니다."),
    INVALID_DAY_DF_WEEK_ENUM_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 DayOfWeek 입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
