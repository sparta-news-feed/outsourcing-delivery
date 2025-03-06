package com.outsourcingdelivery.domain.store.service;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.enums.StoreStatus;
import com.outsourcingdelivery.domain.store.repository.StoreRepository;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import com.outsourcingdelivery.domain.storeSchedule.repository.StoreScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreStatusScheduler {
    private final StoreRepository storeRepository;
    private final StoreScheduleRepository storeScheduleRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateStoreStatus() {
        LocalTime now = LocalTime.now();
        String currentDayAbbreviation  = LocalDate.now().getDayOfWeek().toString().substring(0, 3);

        DayOfWeek currentDay = DayOfWeek.valueOf(currentDayAbbreviation);

        List<StoreSchedule> schedules = storeScheduleRepository.findAllWhereDeletedAtNotNull();

        for (StoreSchedule schedule : schedules) {
            Store store = schedule.getStore();
            LocalTime openTime = schedule.getOpenTime();
            LocalTime closeTime = schedule.getCloseTime();
            DayOfWeek storeDay = schedule.getDayOfWeek();

            if (currentDay.equals(storeDay)) {
                if (now.isAfter(openTime) && now.isBefore(closeTime)) {
                    store.setStoreStatus(StoreStatus.OPEN);
                } else {
                    store.setStoreStatus(StoreStatus.READY);
                }
            }
        }

        storeRepository.saveAll(schedules.stream().map(StoreSchedule::getStore).toList());
        log.info("매장 상태가 영업시간에 따라 업데이트 됩니다.");
    }
}
