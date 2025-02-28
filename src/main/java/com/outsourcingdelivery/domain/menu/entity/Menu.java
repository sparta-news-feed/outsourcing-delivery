package com.outsourcingdelivery.domain.menu.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
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

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Long storeId;

    @Builder
    public Menu(String menuName, int price, String description, Long storeId) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.storeId = storeId;
    }
}
