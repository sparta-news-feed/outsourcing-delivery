package com.outsourcingdelivery.domain.store.repository;

import com.outsourcingdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
