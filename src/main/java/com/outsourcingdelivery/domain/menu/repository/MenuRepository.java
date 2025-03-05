package com.outsourcingdelivery.domain.menu.repository;

import com.outsourcingdelivery.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("SELECT m FROM Menu m JOIN FETCH m.store WHERE m.menuId = :menuId")
    Optional<Menu> findMenuWithStoreById(@Param("menuId") Long menuId);
}
