package com.outsourcingdelivery.domain.menu.service;

import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createMenu(AuthUser authUser, Long storeId, @Valid MenuSaveRequest request) {

        Store store = getStoreByIdOrThrow(storeId);

        Menu menu = new Menu(
                request.getMenuName(),
                request.getPrice(),
                request.getDescription(),
                store
        );

        menuRepository.save(menu);
    }

    @Transactional
    public void updateMenu(AuthUser authUser, Long storeId, Long menuId, @Valid MenuSaveRequest request) {

        Store store = getStoreByIdOrThrow(storeId);
        Menu menu = menuRepository.findByIdOrElseThrow(menuId, ErrorCode.MENU_NOT_FOUND);

        if (!store.getStoreId().equals(menu.getStore().getStoreId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_MENU_UPDATE);
        }

        menu.update(
                request.getMenuName(),
                request.getPrice(),
                request.getDescription()
        );
    }

    private Store getStoreByIdOrThrow(Long storeId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);
        return store;
    }
}
