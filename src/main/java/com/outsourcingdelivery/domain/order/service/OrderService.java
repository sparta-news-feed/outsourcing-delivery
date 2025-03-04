package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderCreateResponse createOrder(OrderCreateRequest requestDto) {
        Order newOrder = new Order(
                requestDto.getAmount()
        );

        Order savedOrder = orderRepository.save(newOrder);

        return new OrderCreateResponse(savedOrder);
    }
}
