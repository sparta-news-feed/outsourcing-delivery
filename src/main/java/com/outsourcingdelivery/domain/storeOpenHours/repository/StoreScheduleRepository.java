package com.outsourcingdelivery.domain.storeOpenHours.repository;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeOpenHours.entity.StoreSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreScheduleRepository extends JpaRepository<StoreSchedule, Long> {
    List<StoreSchedule> findByStore(Store store);
}
