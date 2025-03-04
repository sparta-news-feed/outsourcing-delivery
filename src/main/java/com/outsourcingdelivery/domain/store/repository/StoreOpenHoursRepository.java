package com.outsourcingdelivery.domain.store.repository;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.store.entity.StoreOpenHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreOpenHoursRepository extends JpaRepository<StoreOpenHours, Long> {
    List<StoreOpenHours> findByStore(Store store);
}
