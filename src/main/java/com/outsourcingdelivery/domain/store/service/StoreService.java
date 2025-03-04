package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.dto.response.GetAllStoresResponse;
import com.outsourcingdelivery.domain.store.dto.response.GetStoreResponse;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.dto.Response.StoreScheduleResponse;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStore(AuthUser authUser, String storeName, Integer minOrderPrice, String phoneNumber, String address) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        List<Store> stores = storeRepository.findByUser(user);
        if (stores.size() >= 3) {
            throw new ApplicationException(ErrorCode.MAXIMUM_STORES_IS_THREE);
        }

        Store store = new Store(
                storeName,
                minOrderPrice,
                phoneNumber,
                address,
                user
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
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STORE_VALUE));
        List<StoreScheduleResponse> storeSchedules = storeScheduleRepository.findByStore(store).stream()
                .map(storeSchedule -> new StoreScheduleResponse(
                        storeSchedule.getDayOfWeek(),
                        storeSchedule.getOpenTime(),
                        storeSchedule.getCloseTime()
                ))
                .collect(Collectors.toList());

        return new GetStoreResponse(store, storeSchedules);
    }
}
