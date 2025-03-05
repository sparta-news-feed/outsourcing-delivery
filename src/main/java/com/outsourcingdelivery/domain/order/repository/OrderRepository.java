package com.outsourcingdelivery.domain.order.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order, Long> {

    Optional<Order> findByOrderNo(Long orderNo);

    @Query("""
        SELECT o FROM Order o 
        JOIN FETCH o.menu m
        JOIN FETCH m.store s 
        WHERE o.user.userId = :userId
    """)
    Page<Order> findAllByUserId(Pageable pageable, @Param("userId") Long userId);

    @Query("""
        SELECT o FROM Order o 
        JOIN FETCH o.user u 
        JOIN FETCH o.menu m 
        JOIN FETCH u.primaryAddress a 
        WHERE o.menu.store.storeId = :storeId
    """)
    Page<Order> findAllByStoreId(Pageable pageable, @Param("storeId") Long storeId);

    @Query("""
        SELECT o FROM Order o 
        JOIN FETCH o.menu m
        JOIN FETCH m.store s 
        WHERE o.orderNo = :orderNo
    """)
    Optional<Order> findByOrderNoWithStore(@Param("orderNo") Long orderNo);
}
