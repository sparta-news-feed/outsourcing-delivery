package com.outsourcingdelivery.domain.menu.service;

import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

}
