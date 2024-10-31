package com.spring.sap.controller;

import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.repository.TransactionRecordRepository;
import org.springframework.format.annotation.DateTimeFormat;
import com.spring.sap.entity.Item;
import com.spring.sap.repository.ItemRepository;
import com.spring.sap.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;



@Controller
public class PageController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransactionRecordRepository transactionRepository;

    // 메인 페이지
    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    // 아이템 관리 페이지 이동
    @GetMapping("/items")
    public String itemsHome() {
        return "items_home";
    }

    // 아이템 목록 페이지
    @GetMapping("/items/list")
    public String listItems(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "items";
    }

    // 파일 업로드 페이지
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("message", "엑셀 파일을 선택해 주세요.");
        return "upload";
    }

    // 파일 업로드 및 데이터 저장
    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            excelService.processExcelData(inputStream); // Excel 데이터 처리
            model.addAttribute("message", "Excel file processed successfully!");
            return new ResponseEntity<>("Excel file processed successfully!", HttpStatus.OK);
        } catch (Exception e) {
            model.addAttribute("message", "Failed to process Excel file.");
            return new ResponseEntity<>("Failed to process Excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 재고 관리 페이지 이동
    @GetMapping("/stock_management")
    public String stockManagementPage() {
        return "stock_management";
    }

    // 거래 목록 조회 페이지 이동 및 데이터 전달 추가
    @GetMapping("/transaction_list")
    public String transactionListPage(Model model) {
        List<TransactionRecord> transactions = transactionRepository.findAll();
        model.addAttribute("transactions", transactions);
        return "transaction_list";
    }

    // 거래 목록 조회 페이지 필터링
    @GetMapping("/items/filter")
    public String filterTransactions(
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "parts", required = false) String parts,
            @RequestParam(name = "maker", required = false) String maker,
            @RequestParam(name = "profitPositive", required = false) Boolean profitPositive,
            Model model) {

        // 필터링된 거래 목록을 조회합니다.
        List<TransactionRecord> transactions = transactionRepository.findFilteredTransactions(month, parts, maker, profitPositive);
        model.addAttribute("transactions", transactions);
        return "transaction_list";
    }

    // 수동 거래 등록 페이지
    @GetMapping("/manual_transaction")
    public String manualTransactionPage() {
        return "manual_transaction";
    }

    // 거래 추가
    @PostMapping("/addTransaction")
    public String addTransaction(@RequestParam String itemId,
                                 @RequestParam String transactionType,
                                 @RequestParam Integer quantity,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate transactionDate) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Item ID"));

        TransactionRecord transaction = new TransactionRecord();
        transaction.setItem(item);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(transactionDate);

        if (transactionType.equals("purchase")) {
            if (item.getPurchasePrice() != null && quantity != null) {
                transaction.setPurchaseQuantity(quantity);
                transaction.setPurchasePrice(item.getPurchasePrice());
                transaction.setTotalPrice(item.getPurchasePrice() * quantity);
            } else {
                // 로그를 추가하여 어떤 값이 NULL인지 확인
                System.out.println("Purchase Price or Quantity is NULL for Item ID: " + itemId);
            }
        } else if (transactionType.equals("sale")) {
            if (item.getSellPrice() != null && quantity != null) {
                transaction.setSellQuantity(quantity);
                transaction.setSellPrice(item.getSellPrice());
                transaction.setTotalPrice(item.getSellPrice() * quantity);
            } else {
                // 로그를 추가하여 어떤 값이 NULL인지 확인
                System.out.println("Sell Price or Quantity is NULL for Item ID: " + itemId);
            }
        }


        // 거래를 DB에 저장
        System.out.println("Transaction Type: " + transactionType);
        System.out.println("Quantity: " + quantity);
        System.out.println("Purchase Price: " + item.getPurchasePrice());
        System.out.println("Sell Price: " + item.getSellPrice());

        transactionRepository.save(transaction);
        return "redirect:/transaction_list";
    }







    // 아이템 목록 전체 삭제
    @PostMapping("/items/deleteAll")
    public String deleteAllItems() {
    	transactionRepository.deleteAll();
        itemRepository.deleteAll();  // 데이터베이스의 모든 항목 삭제
        itemRepository.resetAutoIncrement();  // AUTO_INCREMENT 초기화
        return "redirect:/items/list";  // 삭제 후 아이템 목록 페이지로 리다이렉트
    }
}
