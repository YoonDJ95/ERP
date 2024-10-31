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
import org.springframework.http.HttpStatus;


import java.io.InputStream;
import java.util.List;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ItemRepository itemRepository;

    // 파일 업로드 및 데이터 저장
    @PostMapping("/upload")
    public ResponseEntity<String> uploadItemsFromExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            excelService.processExcelData(inputStream); // Excel 데이터 처리
            return new ResponseEntity<>("Excel file processed and items saved successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process Excel file for items", HttpStatus.INTERNAL_SERVER_ERROR);
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
