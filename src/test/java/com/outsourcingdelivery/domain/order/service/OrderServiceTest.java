package com.outsourcingdelivery.domain.order.service;

import com.outsourcingdelivery.common.config.PasswordEncoder;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.order.dto.request.OrderCreateRequest;
import com.outsourcingdelivery.domain.order.dto.response.OrderCreateResponse;
import com.outsourcingdelivery.domain.order.entity.Order;
import com.outsourcingdelivery.domain.order.enums.OrderStatus;
import com.outsourcingdelivery.domain.order.repository.OrderRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.enums.UserType;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
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

    @Autowired
    PasswordEncoder passwordEncoder;

    private User owner;
    private User user;
    private Store store;
    private Menu menu;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("Password1234!"))
                .userType(UserType.USER)
                .phoneNumber("01012345678")
                .username("홍길동")
                .build();

        owner = User.builder()
                .email("owner@test.com")
                .password(passwordEncoder.encode("Password1234!"))
                .userType(UserType.OWNER)
                .phoneNumber("01012345678")
                .username("홍길동")
                .build();

        store = Store.builder()
                .storeName("가게명")
                .minOrderPrice(10000)
                .user(owner)
                .build();

        menu = Menu.builder()
                .menuName("메뉴명")
                .price(10000)
                .description("메뉴설명")
                .store(store)
                .build();
    }
    
    @Test
    @DisplayName("주문 성공")
    void createOrder1() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        User saveOwner = userRepository.save(owner);

        Store saveStore = storeRepository.save(store);
        saveStore.setStoreStatus(StoreStatus.OPEN);
        storeRepository.save(saveStore);

        Menu saveMenu = menuRepository.save(menu);

        AuthUser authUser = AuthUser.builder()
                .userId(saveUser.getUserId())
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(saveStore.getStoreId())
                .menuId(saveMenu.getMenuId())
                .amount(1)
                .build();

        // when
        OrderCreateResponse response = orderService.createOrder(authUser, request);
        Order order = orderRepository.findByIdOrElseThrow(response.getOrderNo(), ErrorCode.NOT_FOUND_ORDER);
        
        // then
        assertThat(order)
                .extracting("orderNo", "orderStatus", "amount", "user", "menu")
                .containsExactly(response.getOrderNo(), OrderStatus.ORDERED, request.getAmount(), saveUser, saveMenu);
    }

    @Test
    @DisplayName("가게가 오픈 상태가 아니라면 예외가 발생한다.")
    void createOrder2() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        User saveOwner = userRepository.save(owner);

        Store saveStore = storeRepository.save(store);

        Menu saveMenu = menuRepository.save(menu);

        AuthUser authUser = AuthUser.builder()
                .userId(saveUser.getUserId())
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(saveStore.getStoreId())
                .menuId(saveMenu.getMenuId())
                .amount(1)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(authUser, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.STORE_NOT_OPEN.getMessage());
    }

    @Test
    @DisplayName("주문 가격이 가게 최소 주문금액보다 작으면 예외가 발생한다.")
    void createOrder3() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        User saveOwner = userRepository.save(owner);

        Store saveStore = storeRepository.save(store);
        saveStore.setStoreStatus(StoreStatus.OPEN);
        storeRepository.save(saveStore);

        Menu saveMenu = menuRepository.save(menu);

        AuthUser authUser = AuthUser.builder()
                .userId(saveUser.getUserId())
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(saveStore.getStoreId())
                .menuId(saveMenu.getMenuId())
                .amount(0)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(authUser, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.MIN_ORDER_PRICE_NOT_MET.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 메뉴를 주문하면 예외가 발생한다.")
    void createOrder4() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        User saveOwner = userRepository.save(owner);

        Store saveStore = storeRepository.save(store);
        saveStore.setStoreStatus(StoreStatus.OPEN);
        storeRepository.save(saveStore);

        AuthUser authUser = AuthUser.builder()
                .userId(saveUser.getUserId())
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(saveStore.getStoreId())
                .menuId(-1L) // 존재하지 않는 메뉴 ID
                .amount(1)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(authUser, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MENU.getMessage());
    }

    @Test
    @DisplayName("가게와 연관되지 않은 메뉴를 주문하면 예외가 발생한다.")
    void createOrder5() throws Exception {
        // given
        User saveUser = userRepository.save(user);
        User saveOwner = userRepository.save(owner);

        Store saveStore = storeRepository.save(store);
        saveStore.setStoreStatus(StoreStatus.OPEN);
        storeRepository.save(saveStore);
        Store anotherStore = Store.builder()
                .storeName("다른 가게")
                .minOrderPrice(5000)
                .user(saveOwner)
                .build();
        storeRepository.save(anotherStore);

        Menu anotherMenu = Menu.builder()
                .menuName("다른 메뉴")
                .price(5000)
                .store(anotherStore)
                .build();
        menuRepository.save(anotherMenu);

        AuthUser authUser = AuthUser.builder()
                .userId(saveUser.getUserId())
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(saveStore.getStoreId())
                .menuId(anotherMenu.getMenuId())
                .amount(1)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(authUser, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.INVALID_MENU_FOR_STORE.getMessage());
    }
}