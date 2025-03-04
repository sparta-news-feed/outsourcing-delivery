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
    private UserType userType;

    @Builder
    private AuthUser(Long userId, UserType userType) {
        this.userId = userId;
        this.userType = userType;
    }

}
