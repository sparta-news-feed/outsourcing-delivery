package com.outsourcingdelivery.domain.storeSchedule.repository;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.entity.StoreSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreScheduleRepository extends JpaRepository<StoreSchedule, Long> {
    List<StoreSchedule> findByStore(Store store);
}
