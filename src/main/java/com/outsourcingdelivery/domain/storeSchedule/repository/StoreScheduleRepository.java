package com.outsourcingdelivery.domain.storeSchedule.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
//import com.outsourcingdelivery.domain.store.entity.StoreSchedule;
import com.outsourcingdelivery.domain.storeSchedule.entity.StoreSchedule;

import java.util.List;

public interface StoreScheduleRepository extends BaseRepository<StoreSchedule, Long> {
    List<StoreSchedule> findAllByStore(Store store);
}
