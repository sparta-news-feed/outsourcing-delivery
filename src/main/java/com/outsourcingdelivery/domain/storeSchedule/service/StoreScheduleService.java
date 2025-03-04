package com.outsourcingdelivery.domain.storeSchedule.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequst;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.UpdateStoreScheduleRequest;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreScheduleService {
    private final StoreScheduleRepository storeScheduleRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStoreSchedule(AuthUser authUser, Long storeId, CreateStoreScheduleRequst dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.INVALID_STORE_VALUE);

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_SCHEDULE_CREATE);
        }

        if (store.isDeleted()) {
            throw new ApplicationException(ErrorCode.STORE_ALREADY_DELETED);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        StoreSchedule storeSchedule = new StoreSchedule(
                DayOfWeek.of(dto.getDayOfWeek()),
                LocalTime.parse(dto.getOpenTime(), formatter),
                LocalTime.parse(dto.getCloseTime(), formatter),
                store
        );

        storeScheduleRepository.save(storeSchedule);
    }

    @Transactional
    public void updateStoreSchedule(AuthUser authUser, Long storeScheduleId, UpdateStoreScheduleRequest dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);
        StoreSchedule storeSchedule = storeScheduleRepository.findByIdOrElseThrow(storeScheduleId, ErrorCode.INVALID_STORE_SCHEDULE_VALUE);

        if (!user.getUserId().equals(storeSchedule.getStore().getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        storeSchedule.updateStoreSchedule(
                DayOfWeek.of(dto.getDayOfWeek()),
                LocalTime.parse(dto.getOpenTime(), formatter),
                LocalTime.parse(dto.getCloseTime(), formatter)
        );
    }

    @Transactional
    public void deleteStoreSchedule(AuthUser authUser, Long storeScheduleId) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.USER_NOT_FOUND);
        StoreSchedule storeSchedule = storeScheduleRepository.findByIdOrElseThrow(storeScheduleId, ErrorCode.INVALID_STORE_SCHEDULE_VALUE);

        if (!user.getUserId().equals(storeSchedule.getStore().getUser().getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        storeScheduleRepository.delete(storeSchedule);
    }
}
