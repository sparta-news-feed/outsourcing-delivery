package com.outsourcingdelivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    // A
    ALREADY_DELETED_USER(UNAUTHORIZED, "이미 탈퇴한 사용자입니다."),
    // C
    CANNOT_DELETE_PRIMARY_ADDRESS(BAD_REQUEST, "기본 주소는 삭제할 수 없습니다."),
    // D
    DUPLICATE_EMAIL(CONFLICT, "이미 가입되어있는 이메일 입니다."),
    DELETED_USER_CANNOT_REGISTER(UNAUTHORIZED, "탈퇴한 사용자는 다시 가입할 수 없습니다."),
    // E
    EXPIRED_JWT_TOKEN(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "RefreshToken이 만료되었습니다."),
    // F
    FORBIDDEN_USER_ONLY(FORBIDDEN, "일반 유저만 접근할 수 있습니다."),
    FORBIDDEN_OWNER_ONLY(FORBIDDEN, "가게 사장님만 접근할 수 있습니다."),
    FORBIDDEN_ADDRESS_UPDATE(FORBIDDEN, "자신의 주소만 수정이 가능합니다."),
    FORBIDDEN_ADDRESS_DELETE(FORBIDDEN, "자신의 주소만 삭제가 가능합니다."),
    FORBIDDEN_REVIEW_CREATION(FORBIDDEN, "배달이 완료된 주문에만 리뷰를 작성할 수 있습니다."),
    FORBIDDEN_REVIEW_UPDATE(FORBIDDEN, "자신이 작성한 리뷰만 수정할 수 있습니다."),
    FORBIDDEN_REVIEW_DELETE(FORBIDDEN, "자신이 작성한 리뷰만 삭제할 수 있습니다."),
    FORBIDDEN_REVIEW_EDIT_EXPIRED(FORBIDDEN, "작성 후 3일이 지난 리뷰는 수정할 수 없습니다."),
    // I
    INVALID_USER_ENUM_VALUE(BAD_REQUEST, "유효하지 않은 UserType 입니다."),
    INVALID_STORE_STATUS_ENUM_VALUE(BAD_REQUEST, "유효하지 않은 StoreStatus 입니다."),
    INVALID_DAY_DF_WEEK_ENUM_VALUE(BAD_REQUEST, "유효하지 않은 DayOfWeek 입니다."),
    INVALID_STORE_VALUE(NOT_FOUND, "유효하지 않은 Store 입니다."),
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INVALID_JWT_TOKEN(BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),
    INVALID_JWT_SECRET(BAD_REQUEST, "유효하지 않은 JWT 시크릿 키입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INVALID_AUTH_ANNOTATION_USAGE(BAD_REQUEST, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    INVALID_STORE_SCHEDULE_VALUE(NOT_FOUND, "유효하지 않은 StoreSchedule 입니다."),
    INCORRECT_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    // M
    MISSING_JWT_TOKEN(BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),
    MAX_USER_ADDRESS_LIMIT_EXCEEDED(BAD_REQUEST, "유저는 최대 10개의 주소만 등록할 수 있습니다."),
    MAXIMUM_STORES_IS_THREE(UNPROCESSABLE_ENTITY, "최대 매장 수는 3개입니다."),
    // N
    NOT_FOUND_STORE(NOT_FOUND, "해당 가게를 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(NOT_FOUND, "해당 토큰을 찾을 수 없습니다."),
    NOT_FOUND_USER(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_USER_ADDRESS(NOT_FOUND, "해당 유저 주소를 찾을 수 없습니다."),
    NOT_FOUND_ORDER(NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."),
    // P
    PRIMARY_ADDRESS_ALREADY_SET(CONFLICT, "이미 기본 주소로 설정된 주소입니다."),
    // R
    REQUIRED_JWT_TOKEN(BAD_REQUEST, "JWT 토큰이 필요합니다."),

    // S
    STORE_NOT_FOUND(NOT_FOUND, "등록되지 않은 가게입니다."),
    SAME_AS_OLD_PASSWORD(CONFLICT, "기존 비밀번호와 새 비밀번호가 같으면 안 됩니다."),
    STORE_ALREADY_DELETED(CONFLICT, "이미 폐업한 가게입니다."),
    // U
    UNSUPPORTED_JWT_TOKEN(BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),
    UNAUTHORIZED_STORE_UPDATE(FORBIDDEN, "자신의 가게만 수정이 가능합니다."),
    UNAUTHORIZED_STORE_SCHEDULE_CREATE(FORBIDDEN, "자신의 가게일정만 생성이 가능합니다."),

    /* ORDER 관련 Exception */
    INVALID_ORDER_STATUS(BAD_REQUEST, "유효하지 않은 주문 상태입니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "존재하지 않는 주문번호입니다."),
    INVALID_ORDER_STATUS_TRANSITION(BAD_REQUEST, "해당 주문 상태로 변경할 수 없습니다."),
    FORBIDDEN_ORDER_CANCELLATION(FORBIDDEN, "본인의 주문만 취소할 수 있습니다."),
    INVALID_ORDER_STATUS_FOR_CANCELLATION(BAD_REQUEST, "해당 주문 상태에서는 취소할 수 없습니다."),

    /* Menu 관련 Exception */
    UNAUTHORIZED_MENU_UPDATE(FORBIDDEN, "본인 가게의 메뉴만 수정할 수 있습니다."),
    MENU_ALREADY_DELETED(CONFLICT, "이미 삭제된 메뉴입니다."),
    MENU_NOT_FOUND(NOT_FOUND,  "등록되지 않은 메뉴입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}