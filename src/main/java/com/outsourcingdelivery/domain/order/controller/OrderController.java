package com.outsourcingdelivery.domain.order.controller;


import com.outsourcingdelivery.common.dto.ApiResponse;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<Void>> createOrder(@RequestBody OrderCreateRequest requestDto) {
        orderService.createOrder(requestDto);
        return ResponseEntity.ok(ApiResponse.success("주문에 성공했습니다."));
    }
}
