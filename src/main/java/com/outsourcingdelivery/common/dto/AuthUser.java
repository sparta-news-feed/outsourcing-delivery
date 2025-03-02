package com.outsourcingdelivery.common.dto;

import com.outsourcingdelivery.domain.user.enums.UserType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthUser {

    private Long userId;
    private String email;
    private UserType userType;

    @Builder
    private AuthUser(Long userId, String email, UserType userType) {
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }

}
