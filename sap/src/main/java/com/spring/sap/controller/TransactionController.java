package com.spring.sap.controller;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRecordService transactionService;

    /**
     * 거래 필터 조건에 따른 필터링 메서드
     * @param year - 조회할 연도 (optional)
     * @param month - 조회할 월 (optional)
     * @param parts - 부품명 (optional)
     * @param maker - 제조사 (optional)
     * @param profitPositive - 수익 여부 (optional)
     * @return 필터링된 거래 목록
     */
    @GetMapping("/filter")
    public ResponseEntity<List<TransactionRecord>> filterTransactions(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String parts,
            @RequestParam(required = false) String maker,
            @RequestParam(required = false) Boolean profitPositive) {

        List<TransactionRecord> transactions = transactionService.filterTransactions(year, month, parts, maker, profitPositive);
        return ResponseEntity.ok(transactions);
    }
}
