package com.spring.sap.controller;

import com.spring.sap.entity.*;
import com.spring.sap.service.*;
import com.spring.sap.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.InputStream;

// Controller 클래스, 페이지 이동 및 데이터를 처리하는 역할
@Controller
public class PageController {

    // Excel 파일 처리를 위한 서비스
    @Autowired
    private ExcelService excelService;

    // Item 엔티티 데이터 접근을 위한 Repository
    @Autowired
    private ItemRepository itemRepository;

    // TransactionRecord 엔티티 데이터 접근을 위한 Repository
    @Autowired
    private TransactionRecordRepository transactionRepository;

    /**
     * 메인 페이지로 이동하는 메서드
     * @return index 템플릿 이름
     */
    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    /**
     * 아이템 관리 페이지로 이동하는 메서드
     * @return items_home 템플릿 이름
     */
    @GetMapping("/items")
    public String itemsHome() {
        return "items_home";
    }

    /**
     * 아이템 목록을 조회하고 모델에 추가하여 반환
     * @param model 템플릿으로 데이터 전달을 위한 모델 객체
     * @return items 템플릿 이름
     */
    @GetMapping("/items/list")
    public String listItems(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "items";
    }

    /**
     * 파일 업로드 페이지로 이동
     * @param model 메시지 출력을 위한 모델 객체
     * @return upload 템플릿 이름
     */
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("message", "엑셀 파일을 선택해 주세요.");
        return "upload";
    }

    /**
     * 파일 업로드 후 Excel 데이터를 처리하고 저장
     * @param file 업로드된 파일
     * @param model 메시지 출력을 위한 모델 객체
     * @return 성공/실패 메시지와 상태 코드 반환
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            excelService.processExcelData(inputStream);
            model.addAttribute("message", "Excel file processed successfully!");
            return new ResponseEntity<>("Excel file processed successfully!", HttpStatus.OK);
        } catch (Exception e) {
            model.addAttribute("message", "Failed to process Excel file.");
            return new ResponseEntity<>("Failed to process Excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 재고 관리 페이지로 이동
     * @return stock_management 템플릿 이름
     */
    @GetMapping("/stock_management")
    public String stockManagementPage() {
        return "stock_management";
    }

    /**
     * 거래 목록 페이지로 이동하며, 모든 거래 기록을 모델에 추가하여 반환
     * @param model 거래 목록을 추가할 모델 객체
     * @return transaction_list 템플릿 이름
     */
    @GetMapping("/transaction_list")
    public String transactionListPage(Model model) {
        List<TransactionRecord> transactions = transactionRepository.findAll();
        model.addAttribute("transactions", transactions);
        return "transaction_list";
    }

    // V0.13 우영씨 코드
    /**
     * 새로운 거래를 추가하고 저장
     * @param itemId 아이템 ID
     * @param transactionType 거래 유형 (구매 또는 판매)
     * @param quantity 거래 수량
     * @param transactionDate 거래 날짜
     * @return 거래 목록 페이지로 리다이렉트
     */
    @PostMapping("/addTransaction")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addTransaction(
            @RequestParam("itemId") String itemName,
            @RequestParam("transactionType") String transactionType,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("transactionDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate transactionDate) {
        
        Map<String, String> response = new HashMap<>();
        System.out.println("Received transaction request for itemName: " + itemName);

        // 이름이 일치하는 Item 검색
        List<Item> items = itemRepository.findByNameContainingIgnoreCase(itemName);
        if (items.isEmpty()) {
            System.out.println("Item not found for name: " + itemName);
            response.put("message", "잘못된 제품 이름입니다.");
            System.out.println("Returning error response: " + response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 첫 번째 결과 사용
        Item item = items.get(0);
        System.out.println("Found item: " + item);

        // 새로운 거래 기록 생성 및 설정
        TransactionRecord transaction = new TransactionRecord();
        transaction.setItem(item);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(transactionDate);

        if ("구매".equals(transactionType) && item.getPurchasePrice() != null && quantity != null) {
            transaction.setPurchaseQuantity(quantity);
            transaction.setPurchasePrice(item.getPurchasePrice());
            transaction.setTotalPrice(item.getPurchasePrice() * quantity);
        } else if ("판매".equals(transactionType) && item.getSellPrice() != null && quantity != null) {
            transaction.setSellQuantity(quantity);
            transaction.setSellPrice(item.getSellPrice());
            transaction.setTotalPrice(item.getSellPrice() * quantity);
        }

        System.out.println("Saving transaction record for item: " + itemName);

        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            response.put("message", "서버 오류로 인해 거래 등록에 실패했습니다.");
            System.out.println("Returning error response: " + response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // 성공적으로 등록된 경우 JSON 형태로 메시지 반환
        response.put("message", "거래가 성공적으로 등록되었습니다.");
        System.out.println("Returning success response: " + response); // 응답 데이터 확인
        return ResponseEntity.ok(response);
    }




    /**
     * 아이템과 거래 목록을 전체 삭제하고 AUTO_INCREMENT 초기화
     * @return 아이템 목록 페이지로 리다이렉트
     */
    @PostMapping("/items/deleteAll")
    public String deleteAllItems() {
        transactionRepository.deleteAll();
        transactionRepository.resetAutoIncrement();
        itemRepository.deleteAll();
        itemRepository.resetAutoIncrement();
        return "redirect:/items/list";
    }
}
