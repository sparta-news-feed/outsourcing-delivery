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
        this.storeName = order.getMenu().getStore().getStoreName();
        this.menuName = order.getMenu().getMenuName();
        this.amount = order.getAmount();
        this.totalPrice = calculateTotalPrice(order);
        this.createdAt = order.getCreatedAt();
    }

    private int calculateTotalPrice(Order order) {
        // amount와 price가 null일 가능성이 거의 없음 (DB에서 보장됨)
        // amount: Order 엔티티에서 @Column(nullable = false), OrderCreateRequest에서 @NotNull @Min(1)
        // price: Menu 엔티티에서 @Column(nullable = false), MenuSaveRequest에서 @NotNull @Positive
        return order.getAmount() * order.getMenu().getPrice();
    }
}
