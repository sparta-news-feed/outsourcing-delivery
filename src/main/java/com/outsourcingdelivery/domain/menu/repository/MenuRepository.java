package com.outsourcingdelivery.domain.menu.repository;

import com.outsourcingdelivery.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
