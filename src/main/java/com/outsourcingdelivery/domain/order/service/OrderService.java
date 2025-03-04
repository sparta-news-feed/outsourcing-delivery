package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
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

        if (!UserType.USER.equals(authUser.getUserType())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ORDER_NON_USER);
        }
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        Order newOrder = new Order(
                requestDto.getAmount(),
                user
        );

        Order savedOrder = orderRepository.save(newOrder);

        return new OrderCreateResponse(savedOrder);
    }
}
