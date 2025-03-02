package com.outsourcingdelivery.domain.store.entity;

import com.outsourcingdelivery.domain.store.enums.DayOfWeek;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "storeOpenHours")
@NoArgsConstructor
public class StoreOpenHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeOpenHoursId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
