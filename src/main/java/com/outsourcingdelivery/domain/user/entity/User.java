package com.outsourcingdelivery.domain.user.entity;

import com.outsourcingdelivery.common.entity.BaseEntity;
import jakarta.persistence.*;

@Table(name = "users")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
