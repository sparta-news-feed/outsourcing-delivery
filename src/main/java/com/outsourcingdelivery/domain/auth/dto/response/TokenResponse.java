package com.outsourcingdelivery.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@RequiredArgsConstructor
public class TokenResponse {

    private final String accessToken;

    private final ResponseCookie refreshToken;

}
