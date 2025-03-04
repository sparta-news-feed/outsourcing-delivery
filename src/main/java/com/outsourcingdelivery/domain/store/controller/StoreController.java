package com.outsourcingdelivery.domain.store.controller;

import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.domain.store.dto.request.CreateStoreRequestDto;
import com.outsourcingdelivery.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createStore(@Valid @RequestBody CreateStoreRequestDto dto) {
        storeService.createStore(
                dto.getStoreName(),
                dto.getMinOrderPrice(),
                dto.getPhoneNumber(),
                dto.getAddress()
        );

        return ResponseEntity.ok(ApiResponse.success("가게 생성에 성공했습니다."));
    }
}
