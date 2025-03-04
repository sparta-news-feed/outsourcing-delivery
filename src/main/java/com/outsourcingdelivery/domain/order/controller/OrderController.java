package com.outsourcingdelivery.domain.order.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.User;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.domain.order.dto.response.OrderResponse;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
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

    @User
    @PostMapping()
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderCreateRequest requestDto
    ) {
        OrderCreateResponse orderCreateResponse = orderService.createOrder(authUser, requestDto);
        return ResponseEntity.ok(ApiResponse.success(orderCreateResponse, "주문에 성공했습니다."));
    }

    @User
    @PatchMapping("/{orderNo}/cancel")
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> cancelOrder(
            @Auth AuthUser authUser,
            @PathVariable Long orderNo
    ) {
        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.cancelOrder(authUser, orderNo);
        return ResponseEntity.ok(ApiResponse.success(orderStatusUpdateResponse, "주문 상태 변경에 성공했습니다."));
    }

    @User
    @GetMapping()
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<OrderResponse> response = orderService.getAllOrders(authUser, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
