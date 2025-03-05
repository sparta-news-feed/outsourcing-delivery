package com.outsourcingdelivery.domain.store.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.OwnerOnly;
import com.outsourcingdelivery.common.auth.UserOnly;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.dto.request.CreateStoreRequest;
import com.outsourcingdelivery.domain.store.dto.request.UpdateStoreRequest;
import com.outsourcingdelivery.domain.store.dto.request.createStoreAndScheduleRequest;
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
            @Valid @RequestBody createStoreAndScheduleRequest dto,
            @RequestParam(name = "storeId", required = false) Long storeId
    ) {
        if (dto.getStore() != null && dto.getSchedule() == null && storeId == null) {
            storeService.createStore(authUser, dto.getStore());
            return ResponseEntity.ok(ApiResponse.success("가게 생성에 성공했습니다."));
        }

        if (dto.getStore() != null && dto.getSchedule() != null && storeId == null) {
            Long newStoreId = storeService.createStore(authUser, dto.getStore());
            storeScheduleService.createStoreSchedule(authUser, newStoreId, dto.getSchedule());
            return ResponseEntity.ok(ApiResponse.success("가게 및 일정 생성에 성공했습니다."));
        }

        if (dto.getStore() == null && dto.getSchedule() != null && storeId != null) {
            storeScheduleService.createStoreSchedule(authUser, storeId, dto.getSchedule());
            return ResponseEntity.ok(ApiResponse.success("가게 일정 생성에 성공했습니다."));
        }
        throw new ApplicationException(ErrorCode.CREATE_BED_REQUEST);
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
    public ResponseEntity<ApiResponse<Void>> updateStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreRequest dto
    ) {
        storeService.updateStore(authUser, storeId, dto);
        return ResponseEntity.ok(ApiResponse.success("가게 정보 수정에 성공했습니다."));
    }

    @OwnerOnly
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId
    ) {
        storeService.deleteStore(authUser, storeId);
        return ResponseEntity.ok(ApiResponse.success("가게 폐업 처리에 성공했습니다."));
    }
}
