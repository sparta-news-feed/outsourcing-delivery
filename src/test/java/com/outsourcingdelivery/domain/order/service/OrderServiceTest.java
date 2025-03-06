package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

//@Transactional
class OrderServiceTest extends SpringBootTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    OrderService orderService;

    private User user;
    private Menu menu;
    private Store store;

    @BeforeEach
    void setUp() {

    }
}