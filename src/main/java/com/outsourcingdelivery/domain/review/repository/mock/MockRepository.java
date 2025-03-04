package com.outsourcingdelivery.domain.review.repository.mock;

import com.outsourcingdelivery.domain.review.mock.Order;
import com.outsourcingdelivery.domain.review.mock.Store;

import java.util.List;

public interface MockRepository {

    List<Order> orderRepository = List.of(
        new Order(1L),
        new Order(2L)
    );

    List<Store> storeRepository = List.of(
        new Store(1L),
        new Store(2L)
    );
}
