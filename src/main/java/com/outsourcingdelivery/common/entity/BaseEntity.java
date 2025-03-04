package com.outsourcingdelivery.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
<<<<<<< HEAD
=======
import lombok.Getter;
import lombok.Setter;
>>>>>>> a23162417211d46fb34076d42f4a3e60882bd0a6
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

<<<<<<< HEAD
    private LocalDateTime deletedAt;
=======
    @Setter
    private LocalDateTime deletedAt;

>>>>>>> a23162417211d46fb34076d42f4a3e60882bd0a6
}
