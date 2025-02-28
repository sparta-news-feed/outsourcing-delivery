package com.outsourcingdelivery.domain.auth.controller;

import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.domain.embedded.Address;
import com.outsourcingdelivery.domain.user.dto.request.UserCreateRequest;
import com.outsourcingdelivery.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserCreateRequest request) {
        userService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getUsername(),
                request.getUserType(),
                new Address(request.getCity(), request.getDistrict(), request.getNeighborhood())
        );

        return ResponseEntity.ok(ApiResponse.success("회원 가입에 성공했습니다."));
    }
}
