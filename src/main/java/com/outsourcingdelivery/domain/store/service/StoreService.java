package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.dto.PageResponse;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.menu.dto.response.MenuResponse;
import com.outsourcingdelivery.domain.menu.repository.MenuRepository;
import com.outsourcingdelivery.domain.store.dto.request.CreateStoreRequest;
import com.outsourcingdelivery.domain.store.dto.request.UpdateStoreRequest;
import com.outsourcingdelivery.domain.store.dto.response.GetAllStoresResponse;
import com.outsourcingdelivery.domain.store.dto.response.GetStoreResponse;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.dto.Response.StoreScheduleResponse;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Long createStore(AuthUser authUser, CreateStoreRequest dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);

        List<Store> stores = storeRepository.findByUser(user);
        if (stores.size() >= 3) {
            throw new ApplicationException(ErrorCode.MAXIMUM_STORES_IS_THREE);
        }

        Store store = new Store(
                dto.getStoreName(),
                dto.getMinOrderPrice(),
                dto.getPhoneNumber(),
                dto.getAddress(),
                user
        );
        storeRepository.save(store);
        return store.getStoreId();
    }

    public PageResponse<GetAllStoresResponse> getAll(int page, int size, String search) {
        int adjustedPage = (page > 0) ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(adjustedPage, size, Sort.by("modifiedAt").descending());

        Page<Store> storePage;
        if (search != null && !search.isEmpty()) {
            storePage = storeRepository.findByStoreNameContainingIgnoreCase(search, pageable);
        } else {
            storePage = storeRepository.findAllPage(pageable);
        }

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
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.INVALID_STORE_VALUE);
        List<StoreScheduleResponse> storeSchedules = storeScheduleRepository.findAllByStore(store).stream()
                .map(storeSchedule -> new StoreScheduleResponse(
                        storeSchedule.getDayOfWeek(),
                        storeSchedule.getOpenTime(),
                        storeSchedule.getCloseTime()
                ))
                .collect(Collectors.toList());
        List<MenuResponse> menus = menuRepository.findAllByStore(store);

        return new GetStoreResponse(store, storeSchedules, menus);
    }

    @Transactional
    public void updateStore(AuthUser authUser, Long storeId, UpdateStoreRequest dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        store.update(
                dto.getStoreName(),
                dto.getMinOrderPrice(),
                dto.getPhoneNumber(),
                dto.getAddress()
        );
    }

    @Transactional
    public void deleteStore(AuthUser authUser, Long storeId) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        if (store.isDeleted()) {
            throw new ApplicationException(ErrorCode.STORE_ALREADY_DELETED);
        }

        List<StoreSchedule> schedules = storeScheduleRepository.findAllByStore(store);
        storeScheduleRepository.deleteAll(schedules);

        store.setDeletedAt(LocalDateTime.now());
    }
}
