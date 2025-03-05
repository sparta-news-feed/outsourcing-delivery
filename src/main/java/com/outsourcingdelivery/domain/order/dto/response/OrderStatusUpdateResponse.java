package com.outsourcingdelivery.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outsourcingdelivery.domain.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderStatusUpdateResponse {
    private final Long orderNo;
    private final String orderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime modifiedAt;

    public OrderStatusUpdateResponse(Order order) {
        this.orderNo = order.getOrderNo();
        this.orderStatus = order.getOrderStatus().name();
        this.modifiedAt = order.getModifiedAt();
    }
}
