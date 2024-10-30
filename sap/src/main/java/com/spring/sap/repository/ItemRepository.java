package com.spring.sap.repository;

import com.spring.sap.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // 이름과 제조사를 기반으로 특정 항목의 존재 여부를 확인
    boolean existsByNameAndMaker(String name, String maker);

    // AUTO_INCREMENT를 초기화하는 메서드 (MySQL 전용)
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE item AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
