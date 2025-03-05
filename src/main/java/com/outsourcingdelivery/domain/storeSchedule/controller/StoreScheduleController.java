package com.outsourcingdelivery.domain.storeSchedule.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.OwnerOnly;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequest;
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

    @OwnerOnly
    @PutMapping("/{storeScheduleId}")
    public ResponseEntity<ApiResponse<Void>> updateStoreSchedule(
            @Auth AuthUser authUser,
            @PathVariable Long storeScheduleId,
            @Valid @RequestBody UpdateStoreScheduleRequest dto
    ) {
        storeScheduleService.updateStoreSchedule(authUser, storeScheduleId, dto);
        return ResponseEntity.ok(ApiResponse.success("일정 수정에 성공했습니다."));
    }

    @OwnerOnly
    @DeleteMapping("/{storeScheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreSchedule(
            @Auth AuthUser authUser,
            @PathVariable Long storeScheduleId
    ) {
        storeScheduleService.deleteStoreSchedule(authUser, storeScheduleId);
        return ResponseEntity.ok(ApiResponse.success("일정 삭제에 성공했습니다."));
    }
}
