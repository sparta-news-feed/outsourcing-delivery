package com.outsourcingdelivery.domain.auth.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.auth.dto.request.WithDrawRequest;
import com.outsourcingdelivery.domain.auth.dto.response.RefreshResponse;
import com.outsourcingdelivery.domain.auth.service.AuthService;
import com.outsourcingdelivery.domain.auth.dto.request.SignUpRequest;
import com.outsourcingdelivery.domain.auth.dto.request.SignInRequest;
import com.outsourcingdelivery.domain.auth.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignUpRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원 가입에 성공했습니다."));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody SignInRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.getRefreshToken().toString())
            .body(ApiResponse.success(response.getAccessToken(), "로그인에 성공했습니다"));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Auth AuthUser authUser) {
        ResponseCookie response = authService.logout(authUser);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.toString())
            .body(ApiResponse.success("로그아웃에 성공했습니다."));
    }

    @PostMapping("/auth/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(
        @Auth AuthUser authUser,
        @Valid @RequestBody WithDrawRequest request
    ) {
        ResponseCookie response = authService.withdraw(authUser, request);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.toString())
            .body(ApiResponse.success("회원탈퇴에 성공했습니다."));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refreshToken(
        @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        RefreshResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
