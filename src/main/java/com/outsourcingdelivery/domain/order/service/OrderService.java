package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.request.OrderStatusUpdateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.dto.response.OrderResponse;
import com.outsourcingdelivery.domain.order.dto.response.OrderStatusUpdateResponse;
import com.outsourcingdelivery.domain.order.dto.response.StoreOrderResponse;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public OrderCreateResponse createOrder(AuthUser authUser, OrderCreateRequest requestDto) {

        User user = validateUserExists(authUser);
        Menu menu = validateMenuAndStore(requestDto);
        Store store = menu.getStore();

        validateStoreStatus(store);
        validateMinOrderPrice(store, menu, requestDto.getAmount());

        Order newOrder = new Order(requestDto.getAmount(), user, menu);

        Order savedOrder = orderRepository.save(newOrder);

        return new OrderCreateResponse(savedOrder);
    }

    @Transactional
    public OrderStatusUpdateResponse cancelOrder(AuthUser authUser, Long orderNo) {

        User user = validateUserExists(authUser);
        Order order = validateOrderForCancellation(user, orderNo);

        order.updateStatus(OrderStatus.CANCELED_BY_USER);

        return new OrderStatusUpdateResponse(order);
    }

    @Transactional
    public OrderStatusUpdateResponse updateOrderStatus(AuthUser authUser, OrderStatusUpdateRequest requestDto) {

        User user = validateUserExists(authUser);

        // TODO: 주문한 가게의 사장 계정이 맞는지 확인

        Order order = orderRepository.findByOrderNo(requestDto.getOrderNo()).orElseThrow(
                () -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND)
        );

        OrderStatus newStatus = requestDto.getOrderStatus();

        if (!order.getOrderStatus().canChangeTo(newStatus)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        order.updateStatus(newStatus);

        return new OrderStatusUpdateResponse(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(AuthUser authUser, int page, int size) {

        User user = validateUserExists(authUser);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("createdAt").descending());

        Page<OrderResponse> orderPages = orderRepository.findAllByUser_UserId(pageable, user.getUserId())
                .map(OrderResponse::new);

        return PageResponse.toDto(orderPages);
    }

    @Transactional(readOnly = true)
    public PageResponse<StoreOrderResponse> getAllStoreOrders(AuthUser authUser, Long storeId, int page, int size) {

        User user = validateUserExists(authUser);

        // TODO: 주문한 가게의 사장 계정이 맞는지 확인

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("createdAt").descending());

        // TODO: Menu와의 연관관계를 통해 StoreId를 기준으로 조회
        // Page<Order> orderPages = orderRepository.findAllByStoreId(pageable, storeId);

        return null;
    }

    private User validateUserExists(AuthUser authUser) {
        return userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
    }

    private Menu validateMenuAndStore(OrderCreateRequest requestDto) {
        Menu menu = menuRepository.findMenuWithStoreById(requestDto.getMenuId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.MENU_NOT_FOUND));

        if (!menu.getStore().getStoreId().equals(requestDto.getStoreId())) {
            throw new ApplicationException(ErrorCode.INVALID_MENU_FOR_STORE);
        }

        return menu;
    }

    private void validateMinOrderPrice(Store store, Menu menu, Integer amount) {
        int totalPrice = menu.getPrice() * amount;
        if (totalPrice < store.getMinOrderPrice()) {
            throw new ApplicationException(ErrorCode.MIN_ORDER_PRICE_NOT_MET);
        }
    }

    private void validateStoreStatus(Store store) {
        if (!StoreStatus.OPEN.equals(store.getStoreStatus())) {
            throw new ApplicationException(ErrorCode.STORE_NOT_OPEN);
        }
    }

    private Order validateOrderForCancellation(User user, Long orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(
                () -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND)
        );

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_ORDER_CANCELLATION);
        }

        if (!order.getOrderStatus().equals(OrderStatus.ORDERED)) {
            throw new ApplicationException(ErrorCode.INVALID_ORDER_STATUS_FOR_CANCELLATION);
        }

        return order;
    }
}
