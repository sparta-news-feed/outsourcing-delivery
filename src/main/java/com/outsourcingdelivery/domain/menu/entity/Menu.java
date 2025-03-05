package com.outsourcingdelivery.domain.menu.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.store.entity.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    public void update(
            @NotNull(message = "메뉴 이름 입력은 필수입니다.") @Size(max = 25, message = "메뉴 이름은 최대 25자까지 입력할 수 있습니다.") String menuName,
            @NotNull(message = "메뉴 가격 입력은 필수입니다..") Integer price,
            String description) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
    }
}
