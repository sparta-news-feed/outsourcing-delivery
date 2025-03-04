package com.outsourcingdelivery.domain.order.controller;

import com.outsourcingdelivery.common.auth.Auth;
import com.outsourcingdelivery.common.auth.Owner;
import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.domain.order.dto.request.OrderStatusUpdateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderStatusUpdateResponse;
import com.outsourcingdelivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders/owner")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OrderService orderService;

    @Owner
    @PatchMapping()
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponse>> updateOrderStatus(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderStatusUpdateRequest requestDto
    ) {
        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.updateOrderStatus(authUser, requestDto);
        return ResponseEntity.ok(ApiResponse.success(orderStatusUpdateResponse, "주문 상태 변경에 성공했습니다."));
    }
}
