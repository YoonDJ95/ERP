package com.spring.sap.service;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private TransactionRecordRepository transactionRepository;

    // 필터링된 거래 목록을 조회
    public List<TransactionRecord> getFilteredTransactions(Integer month, String parts, String maker, Boolean profitPositive) {
        return transactionRepository.findFilteredTransactions(month, parts, maker, profitPositive);
    }
}
