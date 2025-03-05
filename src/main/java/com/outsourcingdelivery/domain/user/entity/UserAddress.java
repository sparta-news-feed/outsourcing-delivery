package com.outsourcingdelivery.domain.user.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAddressId;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private UserAddress(String address, User user) {
        this.address = address;
        this.user = user;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateUser(User user) {
        this.user = user;}
}
