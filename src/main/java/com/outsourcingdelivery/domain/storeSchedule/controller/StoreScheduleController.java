package com.outsourcingdelivery.domain.storeSchedule.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequst;
import com.outsourcingdelivery.domain.storeSchedule.service.StoreScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hours")
public class StoreScheduleController {
    private final StoreScheduleService storeScheduleService;

    @PostMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> createStoreSchedule(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @RequestBody CreateStoreScheduleRequst dto
            ) {
        storeScheduleService.createStoreSchedule(authUser, storeId, dto);

        return ResponseEntity.ok(ApiResponse.success("일정생성에 성공하였습니다."));
    }
}
