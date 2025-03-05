package com.outsourcingdelivery.domain.menu.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.SpringBootTestSupport;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
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
class MenuServiceTest extends SpringBootTestSupport {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store store;
    private User user;

    @BeforeEach
    void setupStore() {
        user = User.builder()
                .email("owner@example.com")
                .password("password1234!")
                .username("username")
                .phoneNumber("01000000000")
                .userType(UserType.OWNER)
                .build();
        userRepository.save(user);
        // Store 를 저장소에 저장하여 실제 DB에 반영되도록 함
        store = new Store("가게1", 1000, "01000000000", "주소1", user);
        storeRepository.save(store); // Store 를 먼저 저장
    }

    @DisplayName("메뉴가 정상적으로 생성된다.")
    @Test
    void saveMenu1() {
        // given
        long storeId = store.getStoreId();
        AuthUser authUser = AuthUser.builder()
                .userId(1L)
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 10000, "설명1");

        // when
        menuService.createMenu(authUser, storeId, request);

        // then
        Menu savedMenu = menuRepository.findAll().get(0); // 저장된 메뉴를 조회
        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getMenuName()).isEqualTo("메뉴1");
        assertThat(savedMenu.getPrice()).isEqualTo(10000);
        assertThat(savedMenu.getDescription()).isEqualTo("설명1");
        assertThat(savedMenu.getStore().getStoreId()).isEqualTo(storeId);
    }

    @DisplayName("USER 타입의 사용자가 메뉴 생성 시도 시, INVALID_USER_TYPE 예외가 발생한다.")
    @Test
    void saveMenu_invalidUserType() {
        // given
        long storeId = store.getStoreId();
        AuthUser authUser = AuthUser.builder()
                .userId(1L)
                .userType(UserType.USER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 10000, "설명1");

        // when & then
        assertThatThrownBy(() -> menuService.createMenu(authUser, storeId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.INVALID_USER_TYPE.getMessage());
    }

    @DisplayName("존재하지 않는 storeId로 메뉴 생성 시도 시, STORE_NOT_FOUND 예외가 발생한다.")
    @Test
    void saveMenu_invalidStoreId() {
        // given
        Long invalidStoreId = -1L; // 존재하지 않는 storeId
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 10000, "설명1");

        // when & then
        assertThatThrownBy(() -> menuService.createMenu(authUser, invalidStoreId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.STORE_NOT_FOUND.getMessage() + " id = " + invalidStoreId);

    }

    private MenuSaveRequest createMenuSaveRequest(String menuName, Integer price, String description) {
        return MenuSaveRequest.builder()
                .menuName(menuName)
                .price(price)
                .description(description)
                .build();
    }
}
