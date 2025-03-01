package com.outsourcingdelivery.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshResponse {

    private final String accessToken;
}
