package com.outsourcingdelivery.domain.order.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.entity.UserAddress;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

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

    public Order(int amount, User user, Menu menu) {
        this.amount = amount;
        this.user = user;
        this.menu = menu;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Builder
    private Order(Long orderNo, OrderStatus orderStatus, Integer amount, User user) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.user = user;
    }

    public String getUserName() {
        return user != null ? user.getUsername() : "No User Info";
    }

    public String getPhoneNumber() {
        return user != null ? user.getPhoneNumber() : "No Phone Number";
    }

    public String getAddress() {
        return Optional.ofNullable(user)
                .map(User::getPrimaryAddress)
                .map(UserAddress::getAddress)
                .orElse("No Address Provided");
    }

    public String getMenuName() {
        return menu != null ? menu.getMenuName() : "No Menu Info";
    }

    public Integer getPrice() {
        return menu != null ? menu.getPrice() : 0;
    }

    public String getStoreName() {
        return Optional.ofNullable(menu)
                .map(Menu::getStore)
                .map(Store::getStoreName)
                .orElse("No Store Info");
    }
}
