package com.outsourcingdelivery.domain.storeSchedule.service;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.storeSchedule.dto.request.CreateStoreScheduleRequst;
//import com.outsourcingdelivery.domain.store.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
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

    @Transactional
    public void createStoreSchedule(Long storeId, CreateStoreScheduleRequst dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STORE_VALUE, "가게를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        StoreSchedule storeSchedule = new StoreSchedule(
                DayOfWeek.of(dto.getDayOfWeek()),
                LocalTime.parse(dto.getOpenTime(), formatter),
                LocalTime.parse(dto.getCloseTime(), formatter),
                store
        );

        storeScheduleRepository.save(storeSchedule);
    }
}
