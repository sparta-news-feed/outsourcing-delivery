package com.outsourcingdelivery.domain.menu.repository;

import com.outsourcingdelivery.common.repository.BaseRepository;
import com.outsourcingdelivery.domain.menu.dto.response.MenuResponse;
import com.outsourcingdelivery.domain.menu.entity.Menu;
import com.outsourcingdelivery.domain.store.entity.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends BaseRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m JOIN FETCH m.store WHERE m.menuId = :menuId")
    Optional<Menu> findMenuWithStoreById(@Param("menuId") Long menuId);

    @Query("SELECT new com.outsourcingdelivery.domain.menu.dto.response.MenuResponse(m.menuId, m.menuName, m.price, m.description) " +
            "FROM Menu m WHERE m.store = :store AND m.deletedAt IS NULL")
    List<MenuResponse> findAllByStore(Store store);
}
