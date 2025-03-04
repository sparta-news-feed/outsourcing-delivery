package com.outsourcingdelivery.domain.order.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.request.OrderStatusUpdateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.dto.response.OrderStatusUpdateResponse;
import com.outsourcingdelivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderCreateRequest requestDto
    ) {
        OrderCreateResponse orderCreateResponse = orderService.createOrder(authUser, requestDto);
        return ResponseEntity.ok(ApiResponse.success(orderCreateResponse, "주문에 성공했습니다."));
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> updateOrderStatus(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderStatusUpdateRequest requestDto
    ) {
        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.updateOrderStatus(authUser, requestDto);
        return ResponseEntity.ok(ApiResponse.success(orderStatusUpdateResponse, "주문 상태 변경에 성공했습니다."));
    }

    @PatchMapping("/{orderNo}")
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> cancelOrderByUser(
            @Auth AuthUser authUser,
            @PathVariable Long orderNo
    ) {
        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.cancelOrderByUser(authUser, orderNo);
        return ResponseEntity.ok(ApiResponse.success(orderStatusUpdateResponse, "주문 상태 변경에 성공했습니다."));
    }
}
