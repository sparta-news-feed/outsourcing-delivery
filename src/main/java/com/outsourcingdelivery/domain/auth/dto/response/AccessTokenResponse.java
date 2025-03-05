package com.outsourcingdelivery.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessTokenResponse {

    private final String accessToken;

    public static AccessTokenResponse toDto(TokenResponse response) {
        return new AccessTokenResponse(response.getAccessToken());
    }
}
