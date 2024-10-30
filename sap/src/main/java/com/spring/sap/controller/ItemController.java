package com.spring.sap.controller;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.entity.Item;
import com.spring.sap.service.ItemService;
import com.spring.sap.repository.ItemRepository;
import com.spring.sap.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ItemRepository itemRepository;

    // 파일 업로드 및 데이터 저장
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            excelService.saveItemsFromExcel(file.getInputStream());
            response.put("message", "파일이 업로드되고 데이터가 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    // 데이터 조회 API
    @GetMapping("/list")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    @Autowired
    private ItemService itemService;
    
    @GetMapping("/filter")
    public ResponseEntity<List<TransactionRecord>> filterTransactions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String parts,
            @RequestParam(required = false) String maker,
            @RequestParam(required = false) Boolean profitPositive) {

        List<TransactionRecord> transactions = itemService.getFilteredTransactions(month, parts, maker, profitPositive);
        return ResponseEntity.ok(transactions);
    }

 // 아이템 관리 페이지 (리스트 페이지)
    @GetMapping("/items")
    public String itemsHome() {
        return "items_home";
    }

    // 아이템 목록 보기 페이지
    @GetMapping("/items/list")
    public String viewItems() {
        return "items";
    }

    // 엑셀 업로드 페이지
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }
}
