package com.outsourcingdelivery.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthUser {

    private Long userId;

    @Builder
    private AuthUser(Long userId) {
        this.userId = userId;
    }

}
