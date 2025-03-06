package com.outsourcingdelivery.domain.auth.controller;

import com.outsourcingdelivery.common.annotation.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.auth.dto.request.WithDrawRequest;
import com.outsourcingdelivery.domain.auth.dto.response.AccessTokenResponse;
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

import java.time.LocalDateTime;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignUpRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원 가입에 성공했습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@Valid @RequestBody SignInRequest request) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        TokenResponse response = authService.login(request, expiryDate);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.getRefreshToken().toString())
            .body(ApiResponse.success(AccessTokenResponse.toDto(response), "로그인에 성공했습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Auth AuthUser authUser) {
        ResponseCookie response = authService.logout(authUser);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.toString())
            .body(ApiResponse.success("로그아웃에 성공했습니다."));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(
        @Auth AuthUser authUser,
        @Valid @RequestBody WithDrawRequest request
    ) {
        LocalDateTime deletedAt = LocalDateTime.now();
        ResponseCookie response = authService.withdraw(authUser, request, deletedAt);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, response.toString())
            .body(ApiResponse.success("회원탈퇴에 성공했습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refresh(
        @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AccessTokenResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 성공적으로 재발급 되었습니다."));
    }

}
