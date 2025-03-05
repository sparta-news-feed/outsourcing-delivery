package com.outsourcingdelivery.domain.store.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends BaseRepository<Store, Long> {

    @Query("SELECT s FROM Store s WHERE s.deletedAt IS NULL")
    Page<Store> findAllPage(Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.user =:user AND s.deletedAt IS NULL")
    List<Store> findByUser(User user);

    @Query("SELECT s FROM Store s JOIN FETCH s.user WHERE s.storeId = :storeId")
    Optional<Store> findByIdWithUser(@Param("storeId") Long storeId);

    Page<Store> findByStoreNameContainingIgnoreCase(String search, Pageable pageable);
}
