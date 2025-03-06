package com.outsourcingdelivery.domain.user.controller;

import com.outsourcingdelivery.common.annotation.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.user.dto.request.CreateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.request.UpdateUserAddressRequest;
import com.outsourcingdelivery.domain.user.dto.response.UserAddressResponse;
import com.outsourcingdelivery.domain.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping("/users/address")
    public ResponseEntity<ApiResponse<String>> createUserAddress(
        @Auth AuthUser authUser,
        @Valid @RequestBody CreateUserAddressRequest request
    ) {
        userAddressService.createUserAddress(authUser, request);
        return ResponseEntity.ok(ApiResponse.success("새로운 주소 생성에 성공했습니다."));
    }

    @GetMapping("/users/address")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getAllUserAddress(
        @Auth AuthUser authUser
    ) {
        List<UserAddressResponse> response = userAddressService.getAllUserAddress(authUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/address/{addressId}")
    public ResponseEntity<ApiResponse<String>> updateUserAddress(
        @Auth AuthUser authUser,
        @PathVariable("addressId") Long addressId,
        @Valid @RequestBody UpdateUserAddressRequest request
    ) {
        userAddressService.updateUserAddress(authUser, addressId, request);
        return ResponseEntity.ok(ApiResponse.success("주소 변경에 성공했습니다."));
    }

    @DeleteMapping("/users/address/{addressId}")
    public ResponseEntity<ApiResponse<String>> deleteUserAddress(
        @Auth AuthUser authUser,
        @PathVariable("addressId") Long addressId
    ) {
        userAddressService.deleteUserAddress(authUser, addressId);
        return ResponseEntity.ok(ApiResponse.success("주소 삭제에 성공했습니다."));
    }
}
