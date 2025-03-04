package com.outsourcingdelivery.domain.menu.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Menu extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false, length = 25)
    private String menuName;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Setter
    private LocalDateTime deletedAt;

    @Builder
    public Menu(String menuName, int price, String description, Store store) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.store = store;
    }
}
