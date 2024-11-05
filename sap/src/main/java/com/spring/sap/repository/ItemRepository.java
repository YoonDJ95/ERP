package com.spring.sap.repository;

import com.spring.sap.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    /**
     * 특정 이름과 제조사를 기반으로 아이템 존재 여부를 확인하는 메서드
     * @param name 아이템 이름
     * @param maker 제조사
     * @return 아이템이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByNameAndMaker(String name, String maker);

    /**
     * 필터링 조건에 맞는 아이템을 검색하는 메서드
     * @param name 아이템 이름 (null이면 모든 이름 포함)
     * @param parts 부품명 (null이면 모든 부품 포함)
     * @param maker 제조사 (null이면 모든 제조사 포함)
     * @param minPurchasePrice 최소 매입 가격 (null이면 제한 없음)
     * @param maxPurchasePrice 최대 매입 가격 (null이면 제한 없음)
     * @param minSellPrice 최소 판매 가격 (null이면 제한 없음)
     * @param maxSellPrice 최대 판매 가격 (null이면 제한 없음)
     * @param performance 성능 정보 (null이면 모든 성능 포함)
     * @return 필터 조건에 맞는 Item 목록
     */
    @Query("SELECT i FROM Item i " +
            "WHERE (:name IS NULL OR i.name LIKE %:name%) " +
            "AND (:parts IS NULL OR i.parts = :parts) " +
            "AND (:maker IS NULL OR i.maker = :maker) " +
            "AND (:minPurchasePrice IS NULL OR i.purchasePrice >= :minPurchasePrice) " +
            "AND (:maxPurchasePrice IS NULL OR i.purchasePrice <= :maxPurchasePrice) " +
            "AND (:minSellPrice IS NULL OR i.sellPrice >= :minSellPrice) " +
            "AND (:maxSellPrice IS NULL OR i.sellPrice <= :maxSellPrice) " +
            "AND (:performance IS NULL OR i.performance LIKE %:performance%)")
    List<Item> findFilteredItems(
            @Param("name") String name,
            @Param("parts") String parts,
            @Param("maker") String maker,
            @Param("minPurchasePrice") Integer minPurchasePrice,
            @Param("maxPurchasePrice") Integer maxPurchasePrice,
            @Param("minSellPrice") Integer minSellPrice,
            @Param("maxSellPrice") Integer maxSellPrice,
            @Param("performance") String performance);


    /**
     * AUTO_INCREMENT를 초기화하는 메서드 (MySQL 전용)
     * 아이템 테이블의 AUTO_INCREMENT 값을 1로 리셋하여 테이블의 ID 증가값을 초기화함
     */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE item AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
