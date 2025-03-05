package com.outsourcingdelivery.domain.store.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.OwnerOnly;
import com.outsourcingdelivery.common.auth.UserOnly;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.dto.request.StoreAndScheduleRequest;
import com.outsourcingdelivery.domain.store.dto.response.GetAllStoresResponse;
import com.outsourcingdelivery.domain.store.dto.response.GetStoreResponse;
import com.outsourcingdelivery.domain.store.service.StoreService;
import com.outsourcingdelivery.domain.storeSchedule.service.StoreScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;
    private final StoreScheduleService storeScheduleService;

    @OwnerOnly
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createStore(
            @Auth AuthUser authUser,
            @Valid @RequestBody StoreAndScheduleRequest dto,
            @RequestParam(name = "storeId", required = false) Long storeId
    ) {
        String message = storeService.create(authUser, dto, storeId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @UserOnly
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GetAllStoresResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search
    ) {
        PageResponse<GetAllStoresResponse> getStores = storeService.getAll(page, size, search);

        return ResponseEntity.ok(ApiResponse.success(getStores));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<GetStoreResponse>> getStore(@PathVariable Long storeId) {
        GetStoreResponse store = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(store));
    }

    @OwnerOnly
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody StoreAndScheduleRequest dto,
            @RequestParam(name = "scheduleId", required = false) Long scheduleId
    ) {
        String message = storeService.update(authUser, storeId, dto, scheduleId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @OwnerOnly
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @RequestParam(name = "scheduleId", required = false) Long scheduleId
    ) {
        String message = storeService.delete(authUser, storeId, scheduleId);
        return ResponseEntity.ok(ApiResponse.success("message"));
    }
}
