package com.outsourcingdelivery.domain.order.repository;

import com.outsourcingdelivery.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(Long orderNo);
}
