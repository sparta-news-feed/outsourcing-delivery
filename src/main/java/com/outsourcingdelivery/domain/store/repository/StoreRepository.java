package com.outsourcingdelivery.domain.store.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends BaseRepository<Store, Long> {
    @Query("SELECT s FROM Store s WHERE s.deletedAt IS NULL")
    Page<Store> findAllPage(Pageable pageable);
    List<Store> findByUser(User user);
}
