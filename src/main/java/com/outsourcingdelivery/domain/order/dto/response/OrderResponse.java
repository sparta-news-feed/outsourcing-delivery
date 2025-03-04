package com.outsourcingdelivery.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outsourcingdelivery.domain.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private final Long orderNo;
    // TODO: 연관 관계 조회 결과
    //private final String storeName;
    //private final String menuName;
    //private final int totalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public OrderResponse(Order order) {
        this.orderNo = order.getOrderNo();
        this.createdAt = order.getCreatedAt();
    }
}
