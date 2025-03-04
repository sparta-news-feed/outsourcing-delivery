package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    @Transactional
    public void createStore(String storeName, Integer minOrderPrice, String phoneNumber, String address) {
        Store store = new Store(
                storeName,
                minOrderPrice,
                phoneNumber,
                address
        );
        storeRepository.save(store);
    }
}
