package com.outsourcingdelivery.domain.store.entity;

import com.outsourcingdelivery.domain.store.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "store_open_hours")
@NoArgsConstructor
public class StoreOpenHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeOpenHoursId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
