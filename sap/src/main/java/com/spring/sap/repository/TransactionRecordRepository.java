package com.spring.sap.repository;

import com.spring.sap.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    /**
     * transaction_record 테이블의 AUTO_INCREMENT 값을 초기화하는 메서드 (MySQL 전용).
     */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE transaction_record AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
