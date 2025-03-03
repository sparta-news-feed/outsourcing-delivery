package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.domain.store.dto.response.GetAllStoresResponse;
import com.outsourcingdelivery.domain.store.dto.response.StoresPageResponse;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public StoresPageResponse getAll(int page, int size) {
        int adjustedPage = (page > 0) ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(adjustedPage, size, Sort.by("modifiedAt").descending());
        Page<Store> storePage = storeRepository.findAll(pageable);

        Page<GetAllStoresResponse> responseDto = storePage.map(store -> new  GetAllStoresResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getMinOrderPrice(),
                store.getStoreStatus(),
                store.getReviewCount()
        ));

        return new StoresPageResponse(responseDto);
    }
}
