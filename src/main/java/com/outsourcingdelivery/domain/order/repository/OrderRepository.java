package com.outsourcingdelivery.domain.order.repository;

import com.outsourcingdelivery.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(Long orderNo);

    Page<Order> findAllByUser_UserId(Pageable pageable, Long userId);

    /*@Query("SELECT o FROM Order o WHERE o.menu.store.id = :storeId")
    Page<Order> findAllByStoreId(Pageable pageable, @Param("storeId") Long storeId);*/
}
