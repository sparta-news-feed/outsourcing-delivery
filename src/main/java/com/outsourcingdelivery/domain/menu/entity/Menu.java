package com.outsourcingdelivery.domain.menu.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Menu extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;
    private String menuName;
    private int price;
    private String description;

//    @ManyToOne
//    @JoinColumn(name = "store_id")
//    private Store store;

//    @Builder
//    public Menu(String menuName, int price, String description, Store store) {
//        this.menuName = menuName;
//        this.price = price;
//        this.description = description;
//        this.store = store;
//    }
}
