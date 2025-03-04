package com.outsourcingdelivery.domain.store.repository;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByUser(User user);
}
