package com.outsourcingdelivery.domain.order.enums;

public enum OrderStatus {
    ORDERED,    // 주문 완료
    COOKING,    // 조리 시작
    DELIVERED,  // 배달 완료
    CANCELED,   // 주문 취소
    CANCELED_BY_USER;   // 회원이 직접 주문 취소

    public boolean canChangeTo(OrderStatus nextStatus) {
        if (this == nextStatus) {
            return false;
        }

        return switch (this) {
            case ORDERED -> nextStatus == COOKING || nextStatus == CANCELED;
            case COOKING -> nextStatus == DELIVERED || nextStatus == CANCELED;
            case DELIVERED, CANCELED -> false;  // 이미 완료된 주문은 변경 불가
            case CANCELED_BY_USER -> false;     // CANCELED_BY_USER는 사용하지 않음
        };
    }
}
