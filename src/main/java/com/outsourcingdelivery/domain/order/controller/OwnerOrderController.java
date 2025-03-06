package com.outsourcingdelivery.domain.order.controller;

import com.outsourcingdelivery.common.annotation.LogOrderApi;
import com.outsourcingdelivery.common.annotation.Auth;
import com.outsourcingdelivery.common.annotation.OwnerOnly;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.domain.order.dto.request.OrderStatusUpdateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderStatusUpdateResponse;
import com.outsourcingdelivery.domain.order.dto.response.StoreOrderResponse;
import com.outsourcingdelivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/owner")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OrderService orderService;

    @OwnerOnly
    @LogOrderApi
    @PutMapping()
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> updateOrderStatus(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderStatusUpdateRequest requestDto
    ) {
        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.updateOrderStatus(authUser, requestDto);
        return ResponseEntity.ok(ApiResponse.success(orderStatusUpdateResponse, "주문 상태 변경에 성공했습니다."));
    }

    @OwnerOnly
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<PageResponse<StoreOrderResponse>>> getAllStoreOrders(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long storeId
    ) {
        PageResponse<StoreOrderResponse> response = orderService.getAllStoreOrders(authUser, storeId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
