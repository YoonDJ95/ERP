package com.spring.sap.repository;

import com.spring.sap.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// TransactionRecordRepository 인터페이스: TransactionRecord 엔티티에 대한 데이터베이스 액세스 기능을 제공
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    /**
     * 거래 내역을 특정 조건에 따라 필터링하여 검색하는 메서드
     * @param month 조회할 월 (null일 경우 무시)
     * @param parts 부품명 (null일 경우 무시)
     * @param maker 제조사 (null일 경우 무시)
     * @param profitPositive 수익 여부 (null일 경우 무시)
     * @return 필터 조건에 맞는 TransactionRecord 목록
     *
     * 필터 조건:
     * - month: 거래 날짜의 월과 일치하는 거래만 조회 (null일 경우 무시)
     * - parts: 아이템의 부품명과 일치하는 거래만 조회 (null일 경우 무시)
     * - maker: 아이템의 제조사와 일치하는 거래만 조회 (null일 경우 무시)
     * - profitPositive: 수익이 양수 또는 음수인 거래만 조회 (null일 경우 무시)
     */
    @Query("SELECT t FROM TransactionRecord t " +
           "WHERE (:month IS NULL OR FUNCTION('MONTH', t.transactionDate) = :month) " +
           "AND (:parts IS NULL OR t.item.parts = :parts) " +
           "AND (:maker IS NULL OR t.item.maker = :maker) " +
           "AND (:profitPositive IS NULL OR " +
           "(:profitPositive = TRUE AND (t.totalPrice > 0)) " +  // 수익 여부 필터링 (양수 조건)
           "OR (:profitPositive = FALSE AND (t.totalPrice <= 0)))") // 수익 여부 필터링 (음수 조건)
    List<TransactionRecord> findFilteredTransactions(
           @Param("month") Integer month,
           @Param("parts") String parts,
           @Param("maker") String maker,
           @Param("profitPositive") Boolean profitPositive);
}
