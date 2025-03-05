package com.outsourcingdelivery.domain.storeSchedule.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreScheduleRepository extends BaseRepository<StoreSchedule, Long> {
    @Query("SELECT ss FROM StoreSchedule ss WHERE ss.store.deletedAt IS NULL")
    List<StoreSchedule> findAllWhereDeletedAtNotNull();
    List<StoreSchedule> findAllByStore(Store store);
    boolean existsByStoreAndDayOfWeek(Store store, DayOfWeek dayOfWeek);
}
