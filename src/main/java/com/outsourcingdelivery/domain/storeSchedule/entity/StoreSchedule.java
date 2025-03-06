package com.outsourcingdelivery.domain.storeSchedule.entity;

import com.outsourcingdelivery.domain.store.entity.Store;
import com.outsourcingdelivery.domain.storeSchedule.enums.DayOfWeek;
import com.outsourcingdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter
@Entity
@Table(name = "store_schedule")
@NoArgsConstructor
public class StoreSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeScheduleId;

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


    public StoreSchedule(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime, Store store) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime.truncatedTo(ChronoUnit.MINUTES);
        this.closeTime = closeTime.truncatedTo(ChronoUnit.MINUTES);
        this.store = store;
    }

    public void updateStoreSchedule(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime.truncatedTo(ChronoUnit.MINUTES);
        this.closeTime = closeTime.truncatedTo(ChronoUnit.MINUTES);
    }

    public User getUser() {
        return (store != null) ? store.getUser() : null;
    }
}
