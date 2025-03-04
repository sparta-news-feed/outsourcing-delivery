package com.outsourcingdelivery.domain.menu.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.enums.UserType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createMenu(AuthUser authUser, Long storeId, @Valid MenuSaveRequest request) {

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new ApplicationException(ErrorCode.INVALID_STORE_VALUE)
        );

        Menu menu = new Menu(
                request.getMenuName(),
                request.getPrice(),
                request.getDescription(),
                store
        );

        menuRepository.save(menu);
    }
}
