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
import com.outsourcingdelivery.domain.user.enums.UserType;
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

    private Store store;

    @BeforeEach
    void setupStore() {
        // Store 를 저장소에 저장하여 실제 DB에 반영되도록 함
        store = new Store("가게1", 10000, "010-0000-0000", "주소1");
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
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 1000, "설명1");

        // when
        menuService.createMenu(authUser, storeId, request);

        // then
        Menu savedMenu = menuRepository.findAll().get(0); // 저장된 메뉴를 조회
        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getMenuName()).isEqualTo("메뉴1");
        assertThat(savedMenu.getPrice()).isEqualTo(1000);
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
                .hasMessageContaining(ErrorCode.INVALID_USER_TYPE.getMessage());
    }


    //    @DisplayName("메뉴 설명 없이도 메뉴가 정상적으로 생성된다.")
//
//    @DisplayName("storeId에 맞는 가게를 찾지 못하면, INVALID_STORE_VALUE 예외가 발생한다.")

    private MenuSaveRequest createMenuSaveRequest(String menuName, Integer price, String description) {
        return MenuSaveRequest.builder()
                .menuName(menuName)
                .price(price)
                .description(description)
                .build();
    }
}
