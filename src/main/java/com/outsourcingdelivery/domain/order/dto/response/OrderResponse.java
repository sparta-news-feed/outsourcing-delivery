package com.outsourcingdelivery.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outsourcingdelivery.domain.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private final Long orderNo;
    private final String orderStatus;
    private final String storeName;
    private final String menuName;
    private final Integer amount;
    private final int totalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public OrderResponse(Order order) {
        this.orderNo = order.getOrderNo();
        this.orderStatus = order.getOrderStatus().name();
        this.storeName = order.getStoreName();
        this.menuName = order.getMenuName();
        this.amount = order.getAmount();
        this.totalPrice = calculateTotalPrice(order);
        this.createdAt = order.getCreatedAt();
    }

    private int calculateTotalPrice(Order order) {
        return order.getAmount() * order.getPrice();
    }
}
