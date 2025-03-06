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

import java.time.LocalDateTime;

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
    private Menu menu;

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

        menu = new Menu("메뉴1", 10000, "설명1", store);
        menuRepository.save(menu);
    }

    @DisplayName("메뉴가 정상적으로 생성된다.")
    @Test
    void saveMenu1() {
        // given
        long storeId = store.getStoreId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 10000, "설명1");

        // when
        Long menuId = menuService.createMenu(authUser, storeId, request);

        // then
        Menu savedMenu = menuRepository.findByIdOrElseThrow(menuId, ErrorCode.NOT_FOUND_MENU); // 저장된 메뉴를 조회
        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getMenuName()).isEqualTo("메뉴1");
        assertThat(savedMenu.getPrice()).isEqualTo(10000);
        assertThat(savedMenu.getDescription()).isEqualTo("설명1");
        assertThat(savedMenu.getStoreId()).isEqualTo(storeId);
    }

    @DisplayName("가게 사장님이 아닌 사용자가 메뉴 생성 시도 시, FORBIDDEN_OWNER_ONLY 예외가 발생한다.")
    @Test
    void saveMenu_invalidOwner() {
        // given
        Long storeId = store.getStoreId();
        AuthUser authUser = AuthUser.builder()
                .userId(-1L)
                .userType(UserType.OWNER).
                build();
        MenuSaveRequest request = createMenuSaveRequest("메뉴1", 10000, "설명1");
        // when & then
        assertThatThrownBy(() -> menuService.createMenu(authUser, storeId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.FORBIDDEN_OWNER_ONLY.getMessage());

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

    @DisplayName("메뉴가 정상적으로 수정된다.")
    @Test
    void updateMenu_success() {
        // given
        Long storeId = store.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("수정된 메뉴", 20000, "수정된 설명");

        // when
        menuService.updateMenu(authUser, storeId, menuId, request);

        // then
        Menu updatedmenu = menuRepository.findByIdOrElseThrow(menuId, ErrorCode.NOT_FOUND_MENU);
        assertThat(updatedmenu).isNotNull();
        assertThat(updatedmenu.getMenuName()).isEqualTo("수정된 메뉴");
        assertThat(updatedmenu.getPrice()).isEqualTo(20000);
        assertThat(updatedmenu.getDescription()).isEqualTo("수정된 설명");

    }

    @DisplayName("가게 사장님이 아닌 사용자가 메뉴 수정 시도 시, FORBIDDEN_OWNER_ONLY 예외 발생.")
    @Test
    void updateMenu_invalidOwner() throws Exception {
        // given
        Long storeId = store.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(-1L)
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("수정된 메뉴", 20000, "수정된 설명");

        // when & then
        assertThatThrownBy(() -> menuService.updateMenu(authUser, storeId, menuId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.FORBIDDEN_OWNER_ONLY.getMessage());

    }

    @DisplayName("다른 가게의 메뉴 수정 시도 시, UNAUTHORIZED_MENU_UPDATE 예외 발생.")
    @Test
    void updateMenu_unauthorizedStore() {
        // given
        Store anotherStore = new Store("가게2", 2000, "01011111111", "주소2", user);
        storeRepository.save(anotherStore);
        Long anotherStoreId = anotherStore.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();
        MenuSaveRequest request = createMenuSaveRequest("수정된 메뉴", 20000, "수정된 설명");

        // when & then
        assertThatThrownBy(() -> menuService.updateMenu(authUser, anotherStoreId, menuId, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_MENU_UPDATE.getMessage());

    }

    @DisplayName("메뉴가 정상적으로 삭제된다.")
    @Test
    void deleteMenu_success() throws Exception {
        // given
        Long storeId = store.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();

        // when
        menuService.deleteMenu(authUser, storeId, menuId);

        // then
        Menu deletedMenu = menuRepository.findByIdOrElseThrow(menuId, ErrorCode.NOT_FOUND_MENU);
        assertThat(deletedMenu.isDeleted()).isTrue();
        assertThat(deletedMenu.getDeletedAt()).isNotNull();
    }

    @DisplayName("이미 삭제된 메뉴를 다시 삭제하려 할 때, MENU_ALREADY_DELETED 예외 발생")
    @Test
    void deleteMenu_alreadyDeleted() throws Exception {
        // given
        Long storeId = store.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();
        menu.setDeletedAt(LocalDateTime.now());
        menuRepository.save(menu);

        // when & then
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, storeId, menuId))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.MENU_ALREADY_DELETED.getMessage());

    }

    @DisplayName("다른 가게의 메뉴 삭제 시도 시, UNAUTHORIZED_MENU_DELETE 예외 발생")
    @Test
    void deleteMenu_unauthorizedStore() throws Exception {
        // given
        Store anotherStore = new Store("가게2", 20000, "01011111111", "주소1",user);
        storeRepository.save(anotherStore);
        Long anotherStoreId = anotherStore.getStoreId();
        Long menuId = menu.getMenuId();
        AuthUser authUser = AuthUser.builder()
                .userId(user.getUserId())
                .userType(UserType.OWNER)
                .build();

        // when & then
        assertThatThrownBy(() -> menuService.deleteMenu(authUser, anotherStoreId, menuId))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_MENU_DELETE.getMessage());
    }

    private MenuSaveRequest createMenuSaveRequest(String menuName, Integer price, String description) {
        return MenuSaveRequest.builder()
                .menuName(menuName)
                .price(price)
                .description(description)
                .build();
    }
}
