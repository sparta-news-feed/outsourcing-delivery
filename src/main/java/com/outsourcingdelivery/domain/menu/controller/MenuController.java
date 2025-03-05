package com.outsourcingdelivery.domain.menu.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.common.auth.OwnerOnly;
import com.outsourcingdelivery.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MenuController {

    private final MenuService menuService;

    @OwnerOnly
    @PostMapping("/stores/{storeId}/menus")
    public ResponseEntity<ApiResponse<String>> createMenu(
            @Auth AuthUser authUser,
            @PathVariable("storeId") Long storeId,
            @RequestBody MenuSaveRequest request
    ) {
        menuService.createMenu(authUser, storeId, request);
        return ResponseEntity.ok(ApiResponse.success("메뉴 생성에 성공했습니다."));
    }

    //    @Owner
//    @PutMapping // storeId uri 에 넣어주기
}
