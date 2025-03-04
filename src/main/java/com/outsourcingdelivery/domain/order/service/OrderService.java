package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.request.OrderStatusUpdateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.dto.response.OrderStatusUpdateResponse;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderCreateResponse createOrder(AuthUser authUser, OrderCreateRequest requestDto) {

        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        // TODO: 예외처리
        // 가게 오픈/마감 시간 검증
        // 가게 최소 주문 금액 검증

        Order newOrder = new Order(
                requestDto.getAmount(),
                user
        );

        Order savedOrder = orderRepository.save(newOrder);

        return new OrderCreateResponse(savedOrder);
    }

    @Transactional
    public OrderStatusUpdateResponse updateOrderStatus(AuthUser authUser, OrderStatusUpdateRequest requestDto) {

        // TODO: 주문한 가게의 사장 계정이 맞는지 확인

        Order order = orderRepository.findByOrderNo(requestDto.getOrderNo()).orElseThrow(
                () -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND)
        );

        OrderStatus newStatus = OrderStatus.of(requestDto.getOrderStatus());

        if (!order.getOrderStatus().canChangeTo(newStatus)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        order.updateStatus(newStatus);

        return new OrderStatusUpdateResponse(order);
    }

    @Transactional
    public OrderStatusUpdateResponse cancelOrderByUser(AuthUser authUser, Long orderNo) {

        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(
                () -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND)
        );

        if (!order.getUser().getUserId().equals(authUser.getUserId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ORDER_CANCELLATION);
        }

        if (!order.getOrderStatus().equals(OrderStatus.ORDERED)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_STATUS_FOR_CANCELLATION);
        }

        order.updateStatus(OrderStatus.CANCELED_BY_USER);

        return new OrderStatusUpdateResponse(order);
    }
}
