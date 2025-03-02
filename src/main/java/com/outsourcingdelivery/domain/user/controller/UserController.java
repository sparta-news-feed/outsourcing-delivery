package com.outsourcingdelivery.domain.user.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.user.dto.request.UpdatePasswordRequest;
import com.outsourcingdelivery.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping("/users/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
        @Auth AuthUser authUser,
        @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(authUser, request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경에 성공했습니다."));
    }

    @PatchMapping("/users/primary-address/{addressId}")
    public ResponseEntity<ApiResponse<String>> updatePrimaryAddress(
        @Auth AuthUser authUser,
        @PathVariable("addressId") Long addressId
    ){
        userService.updatePrimaryAddress(authUser, addressId);
        return ResponseEntity.ok(ApiResponse.success("기본 주소지 변경에 성공했습니다."));
    }
}

