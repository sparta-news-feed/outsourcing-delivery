package com.outsourcingdelivery.domain.user.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.embedded.Address;
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
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Embedded
    private Address address;

    private LocalDateTime deletedAt;

    public User(String email, String password, String username, UserType userType, Address address) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userType = userType;
        this.address = address;
    }
}
