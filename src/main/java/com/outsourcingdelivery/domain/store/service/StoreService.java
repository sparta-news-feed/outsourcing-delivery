package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.domain.store.dto.response.GetAllStoresResponse;
import com.outsourcingdelivery.domain.store.dto.response.GetStoreResponse;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeOpenHours.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeOpenHours.repository.StoreScheduleRepository;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreScheduleRepository storeScheduleRepository;

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

    public PageResponse<GetAllStoresResponse> getAll(int page, int size) {
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

        return PageResponse.toDto(responseDto);
    }

    public GetStoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + storeId));
        List<StoreSchedule> storeOpenHours = storeScheduleRepository.findByStore(store);
        return new GetStoreResponse(store, storeOpenHours);
    }
}
