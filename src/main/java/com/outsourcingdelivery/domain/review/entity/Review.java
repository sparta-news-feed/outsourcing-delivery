package com.outsourcingdelivery.domain.review.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import com.outsourcingdelivery.domain.review.mock.Order;
import com.outsourcingdelivery.domain.review.mock.Store;
import com.outsourcingdelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contents;

    private Short rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private Long storeId;

    private Long orderId;

    @Builder
    private Review(Long id, String contents, Short rating, User user, Long storeId, Long orderId) {
        this.id = id;
        this.contents = contents;
        this.rating = rating;
        this.user = user;
        this.storeId = storeId;
        this.orderId = orderId;
    }
}
