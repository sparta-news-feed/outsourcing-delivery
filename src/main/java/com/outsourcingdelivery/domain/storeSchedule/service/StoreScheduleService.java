package com.outsourcingdelivery.domain.storeSchedule.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreScheduleService {
    private final StoreScheduleRepository storeScheduleRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStoreSchedule(AuthUser authUser, Long storeId, StoreScheduleRequest dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.INVALID_STORE_VALUE);

        if (!user.getUserId().equals(store.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_SCHEDULE_CREATE);
        }

        if (store.isDeleted()) {
            throw new ApplicationException(ErrorCode.STORE_ALREADY_DELETED);
        }

        if (storeScheduleRepository.existsByStoreAndDayOfWeek(store, dto.getDayOfWeek())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_DAY_OF_WEEK);
        }

        StoreSchedule storeSchedule = new StoreSchedule(
                dto.getDayOfWeek(),
                dto.getOpenTime(),
                dto.getCloseTime(),
                store
        );

        storeScheduleRepository.save(storeSchedule);
    }

    @Transactional
    public void updateStoreSchedule(AuthUser authUser, Long storeScheduleId, StoreScheduleRequest dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        StoreSchedule storeSchedule = storeScheduleRepository.findByIdOrElseThrow(storeScheduleId, ErrorCode.INVALID_STORE_SCHEDULE_VALUE);

        if (storeScheduleRepository.existsByStoreAndDayOfWeek(storeSchedule.getStore(), dto.getDayOfWeek())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_DAY_OF_WEEK);
        }

        if (!user.getUserId().equals(storeSchedule.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        storeSchedule.updateStoreSchedule(
                dto.getDayOfWeek(),
                dto.getOpenTime(),
                dto.getCloseTime()
        );
    }

    @Transactional
    public void deleteStoreSchedule(AuthUser authUser, Long storeScheduleId) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        StoreSchedule storeSchedule = storeScheduleRepository.findByIdOrElseThrow(storeScheduleId, ErrorCode.INVALID_STORE_SCHEDULE_VALUE);

        if (!user.getUserId().equals(storeSchedule.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        storeScheduleRepository.delete(storeSchedule);
    }
}
