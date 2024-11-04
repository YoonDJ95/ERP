package com.spring.sap.service;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    public List<TransactionRecord> filterTransactions(Integer year, Integer month, String parts, String maker, Boolean profitPositive) {
        // 빈 문자열을 null로 처리
        if (parts != null && parts.isEmpty()) parts = null;
        if (maker != null && maker.isEmpty()) maker = null;
        
        // 모든 필터가 null이면 전체 목록 반환
        if (month == null && parts == null && maker == null && profitPositive == null) {
            return transactionRecordRepository.findAll();
        }
        
        return transactionRecordRepository.findFilteredTransactions(year, month, parts, maker, profitPositive);
    }



}
