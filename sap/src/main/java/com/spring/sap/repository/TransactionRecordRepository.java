package com.spring.sap.repository;

import com.spring.sap.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TransactionRecordRepository 인터페이스: TransactionRecord 엔티티에 대한 데이터베이스 액세스 기능을 제공
 */
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    /**
     * 특정 조건에 따라 거래 내역을 필터링하여 검색하는 메서드
     *
     * @param month 조회할 월 (null일 경우 무시)
     * @param parts 부품명 (null일 경우 무시)
     * @param maker 제조사 (null일 경우 무시)
     * @param profitPositive 수익 여부 (null일 경우 무시)
     * @return 필터 조건에 맞는 TransactionRecord 목록
     */
	@Query("SELECT t FROM TransactionRecord t " +
		       "WHERE (:year IS NULL OR FUNCTION('YEAR', t.transactionDate) = :year) " +
		       "AND (:month IS NULL OR FUNCTION('MONTH', t.transactionDate) = :month) " +
		       "AND (:parts IS NULL OR t.item.parts = :parts) " +
		       "AND (:maker IS NULL OR t.item.maker = :maker) " +
		       "AND (:profitPositive IS NULL OR " +
		       "(CASE WHEN :profitPositive = TRUE THEN t.sellQuantity IS NOT NULL " +
		       "ELSE t.purchaseQuantity IS NOT NULL END))")
		List<TransactionRecord> findFilteredTransactions(
		       @Param("year") Integer year,
		       @Param("month") Integer month,
		       @Param("parts") String parts,
		       @Param("maker") String maker,
		       @Param("profitPositive") Boolean profitPositive);



    /**
     * transaction_record 테이블의 AUTO_INCREMENT 값을 초기화하는 메서드 (MySQL 전용).
     */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE transaction_record AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
