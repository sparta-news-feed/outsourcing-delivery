package com.outsourcingdelivery.domain.menu.service;

import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.menu.dto.request.MenuSaveRequest;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
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
    public void createMenu(Long storeId, MenuSaveRequest request) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.NOT_FOUND_STORE);

        Menu menu = Menu.builder()
            .menuName(request.getMenuName())
            .price(request.getPrice())
            .description(request.getDescription())
            .store(store)
            .build();

        menuRepository.save(menu);
    }
}
