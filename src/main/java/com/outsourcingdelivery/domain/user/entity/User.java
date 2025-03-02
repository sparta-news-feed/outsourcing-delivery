package com.outsourcingdelivery.domain.user.entity;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.user.enums.UserType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "user_type"}))
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String username;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_address_id")
    private UserAddress primaryAddress;

    @Builder
    private User(Long userId, String email, String password, String username, String phoneNumber, UserType userType) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public void updatePrimaryAddress(UserAddress primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteUser() {
        primaryAddress = null;
        setDeletedAt(LocalDateTime.now());
    }

}
