package com.outsourcingdelivery.common.repository;

import com.outsourcingdelivery.common.exception.ApplicationException;
import com.outsourcingdelivery.common.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default T findByIdOrElseThrow(ID id, ErrorCode errorCode) {
        return findById(id).orElseThrow(
            () -> new ApplicationException(errorCode, errorCode.getMessage() + " id = " + id)
        );
    }

}
