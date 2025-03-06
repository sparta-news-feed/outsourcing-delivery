package com.outsourcingdelivery.domain.storeSchedule.service;

import com.outsourcingdelivery.common.dto.AuthUser;
import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.StoreScheduleRequest;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
import com.outsourcingdelivery.domain.user.entity.User;
import com.outsourcingdelivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StoreScheduleService {
    private final StoreScheduleRepository storeScheduleRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStoreSchedule(AuthUser authUser, Long storeId, List<StoreScheduleRequest> dtoList) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.INVALID_STORE_VALUE);

        if (!store.getUser().equals(user)) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_SCHEDULE_CREATE);
        }

        if (store.isDeleted()) {
            throw new ApplicationException(ErrorCode.STORE_ALREADY_DELETED);
        }

        List<StoreSchedule> storeSchedules = dtoList.stream()
                        .map(dto -> new StoreSchedule(
                                dto.getDayOfWeek(),
                                dto.getOpenTime(),
                                dto.getCloseTime(),
                                store
                        ))
                        .toList();

        storeScheduleRepository.saveAll(storeSchedules);
    }

    @Transactional
    public void updateStoreSchedule(AuthUser authUser, Long storeId, List<StoreScheduleRequest> dto) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.INVALID_STORE_SCHEDULE_VALUE);
        List<StoreSchedule> storeSchedules = storeScheduleRepository.findAllByStore(store);

        if (!store.getUser().equals(user)) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_STORE_UPDATE);
        }

        if (storeSchedules.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_STORE_SCHEDULE_VALUE);
        }

        for (StoreScheduleRequest request : dto) {
            StoreSchedule scheduleToUpdate = storeSchedules.stream()
                    .filter(schedule -> schedule.getDayOfWeek().equals(request.getDayOfWeek()))
                    .findFirst()
                    .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_DAY_OF_WEEK));

            scheduleToUpdate.updateStoreSchedule(
                    request.getOpenTime(),
                    request.getCloseTime()
            );
        }
    }
}
