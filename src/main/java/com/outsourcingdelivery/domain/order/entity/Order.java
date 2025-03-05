package com.outsourcingdelivery.domain.order.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    private Long orderNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // TODO: 연관관계 설정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "menu_id", nullable = false)
//    private Menu menu;

    @PrePersist
    public void prePersist() {
        // 주문번호 자동 생성
        if (this.orderNo == null) {
            this.orderNo = SnowflakeOrderNoGenerator.generateOrderNo();
        }
        // 상태 기본값 설정
        if (this.orderStatus == null) {
            this.orderStatus = OrderStatus.ORDERED;
        }
    }

    public Order(int amount, User user) {
        this.amount = amount;
        this.user = user;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Builder
    private Order(Long orderNo, OrderStatus orderStatus, int amount) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.amount = amount;
    }
}
