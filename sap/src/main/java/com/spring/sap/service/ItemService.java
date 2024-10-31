package com.spring.sap.service;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ItemService 클래스: 거래 목록 조회 관련 비즈니스 로직을 처리
@Service
public class ItemService {

    // TransactionRecordRepository를 통해 거래 내역에 접근
    @Autowired
    private TransactionRecordRepository transactionRepository;

    /**
     * 필터링된 거래 목록을 조회하는 메서드
     * @param month 조회할 월 (null일 경우 모든 월 포함)
     * @param parts 조회할 부품명 (null일 경우 모든 부품 포함)
     * @param maker 조회할 제조사 (null일 경우 모든 제조사 포함)
     * @param profitPositive 수익 여부 (null일 경우 수익 필터링 안 함)
     * @return 필터 조건에 맞는 거래 목록 반환
     */
    public List<TransactionRecord> getFilteredTransactions(Integer month, String parts, String maker, Boolean profitPositive) {
        return transactionRepository.findFilteredTransactions(month, parts, maker, profitPositive);
    }
}
