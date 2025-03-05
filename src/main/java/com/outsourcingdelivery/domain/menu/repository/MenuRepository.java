package com.outsourcingdelivery.domain.menu.repository;

import com.outsourcingdelivery.domain.menu.dto.response.MenuResponse;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("SELECT new com.outsourcingdelivery.domain.menu.dto.response.MenuResponse(m.menuId, m.menuName, m.price, m.description) " +
            "FROM Menu m WHERE m.store = :store AND m.deletedAt IS NULL")
    List<MenuResponse> findAllByStore(Store store);
}
