package com.outsourcingdelivery.domain.user.entity;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "user_type"}))
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_address_id")
    private UserAddress primaryAddress;

    private LocalDateTime deletedAt;

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
        primaryAddress.updateUser(this);
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteUser(LocalDateTime deletedAt) {
        primaryAddress = null;
        this.deletedAt = deletedAt;
    }

}
