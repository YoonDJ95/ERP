package com.spring.sap.repository;

import com.spring.sap.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    // 거래 내역 필터링 메서드: 월, 부품, 제조사, 수익 여부에 따라 거래 내역 검색
    @Query("SELECT t FROM TransactionRecord t " +
           "WHERE (:month IS NULL OR FUNCTION('MONTH', t.transactionDate) = :month) " +
           "AND (:parts IS NULL OR t.item.parts = :parts) " +
           "AND (:maker IS NULL OR t.item.maker = :maker) " +
           "AND (:profitPositive IS NULL OR " +
           "(:profitPositive = TRUE AND (t.totalPrice > 0)) " +  // 수익 여부에 따른 필터링 조건 수정
           "OR (:profitPositive = FALSE AND (t.totalPrice <= 0)))")
    List<TransactionRecord> findFilteredTransactions(
           @Param("month") Integer month,
           @Param("parts") String parts,
           @Param("maker") String maker,
           @Param("profitPositive") Boolean profitPositive);
}
