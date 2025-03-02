package com.outsourcingdelivery.domain.store.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private Integer minOrderPrice;

    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Long reviewCount;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus;

    @Column(nullable = false)
    private String address;

    public Store(String storeName, Integer minOrderPrice, String phoneNumber, String address) {
        this.storeName = storeName;
        this.minOrderPrice = minOrderPrice;
        this.phoneNumber = phoneNumber;
        this.storeStatus = StoreStatus.READY;
        this.address = address;
        this.reviewCount = 0L;
    }
}
