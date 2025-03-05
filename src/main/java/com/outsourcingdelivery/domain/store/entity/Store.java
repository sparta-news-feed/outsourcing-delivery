package com.outsourcingdelivery.domain.store.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import com.outsourcingdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    //    @Column(nullable = false)
    private String storeName;

    //    @Column(nullable = false)
    private Integer minOrderPrice;

    @Column(columnDefinition = "INT UNSIGNED")
//    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Long reviewCount;

    //    @Column(nullable = false)
    private String phoneNumber;

    //    @Column(nullable = false)
    @Setter
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus;

    //    @Column(nullable = false)
    private String address;

    @Setter
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Store(String storeName, Integer minOrderPrice, String phoneNumber, String address, User user) {
        this.storeName = storeName;
        this.minOrderPrice = minOrderPrice;
        this.phoneNumber = phoneNumber;
        this.storeStatus = StoreStatus.READY;
        this.address = address;
        this.reviewCount = 0L;
        this.user = user;
    }

    public void update(String storeName, Integer minOrderPrice, String phoneNumber, String address) {
        this.storeName = storeName;
        this.minOrderPrice = minOrderPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @Builder
    private Store(Long storeId, String storeName, Integer minOrderPrice, String phoneNumber, String address, User user) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.minOrderPrice = minOrderPrice;
        this.reviewCount = 0L;
        this.phoneNumber = phoneNumber;
        this.storeStatus = StoreStatus.READY;
        this.address = address;
        this.user = user;
    }
}
