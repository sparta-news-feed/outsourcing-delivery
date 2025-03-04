package com.outsourcingdelivery.domain.storeSchedule.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.Owner;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequst;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.UpdateStoreScheduleRequest;
import com.outsourcingdelivery.domain.storeSchedule.service.StoreScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hours")
public class StoreScheduleController {
    private final StoreScheduleService storeScheduleService;

    @Owner
    @PostMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> createStoreSchedule(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody CreateStoreScheduleRequst dto
            ) {
        storeScheduleService.createStoreSchedule(authUser, storeId, dto);

        return ResponseEntity.ok(ApiResponse.success("일정생성에 성공했습니다."));
    }

    @Owner
    @PutMapping("/{storeScheduleId}")
    public ResponseEntity<ApiResponse<Void>> updateStoreSchedule(
            @Auth AuthUser authUser,
            @PathVariable Long storeScheduleId,
            @Valid @RequestBody UpdateStoreScheduleRequest dto
    ) {
        storeScheduleService.updateStoreSchedule(authUser, storeScheduleId, dto);
        return ResponseEntity.ok(ApiResponse.success("일정 수정에 성공했습니다."));
    }
}
