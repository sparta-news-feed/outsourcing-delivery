package com.outsourcingdelivery.domain.menu.controller;

import com.outsourcingdelivery.common.auth.Owner;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Owner
    @PostMapping("/api/v1/stores/{storeId}/menus")
    public ResponseEntity<ApiResponse<String>> createMenu(
        @PathVariable("storeId") Long storeId,
        @RequestBody MenuSaveRequest request
        ) {
        menuService.createMenu(storeId, request);
        return ResponseEntity.ok(ApiResponse.success("메뉴 생성에 성공했습니다."));
    }
}
