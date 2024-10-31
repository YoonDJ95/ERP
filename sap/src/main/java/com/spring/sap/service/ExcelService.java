package com.spring.sap.service;

import org.apache.poi.ss.usermodel.*;
import com.spring.sap.entity.Item;
import com.spring.sap.entity.TransactionRecord;
import com.spring.sap.repository.ItemRepository;
import com.spring.sap.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // 추가해야 하는 import 문
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    // 전체 Excel 파일을 처리하는 메서드
    public void processExcelData(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            saveItemsFromWorkbook(workbook);
            saveTransactionsFromWorkbook(workbook);
        } catch (Exception e) {
            logger.error("Error while processing Excel file", e);
        }
    }

    // Workbook에서 아이템 데이터를 저장하는 메서드
    public void saveItemsFromWorkbook(Workbook workbook) {
        Sheet itemSheet = workbook.getSheetAt(0); // 첫 번째 시트로 접근
        List<Item> items = new ArrayList<>();

        for (Row row : itemSheet) {
            if (row.getRowNum() == 0) continue; // 헤더 스킵

            String itemId = getStringCellValue(row.getCell(0));
            String name = getStringCellValue(row.getCell(1));
            String parts = getStringCellValue(row.getCell(2));
            String maker = getStringCellValue(row.getCell(3));
            Double purchasePrice = getNumericCellValue(row.getCell(4));
            Double sellPrice = getNumericCellValue(row.getCell(5));
            String performance = getStringCellValue(row.getCell(6));

            // 필수 데이터 유효성 검사
            if (itemId == null || name == null || maker == null) {
                logger.warn("Skipping row {}: Missing required data (ID, Name, or Maker)", row.getRowNum());
                continue;
            }

            try {
                Item item = new Item();
                item.setId(itemId);
                item.setName(name);
                item.setParts(parts);
                item.setMaker(maker);
                item.setPurchasePrice(purchasePrice != null ? purchasePrice : 0.0);
                item.setSellPrice(sellPrice != null ? sellPrice : 0.0);
                item.setPerformance(performance);

                items.add(item);
            } catch (Exception e) {
                logger.warn("Error processing item row: " + row.getRowNum(), e);
            }
        }

        itemRepository.saveAll(items);
        logger.info("Saved {} items to the database.", items.size());
    }

    
 // Workbook에서 거래 데이터를 저장하는 메서드
    public void saveTransactionsFromWorkbook(Workbook workbook) {
        Sheet transactionSheet = workbook.getSheetAt(1);
        List<TransactionRecord> transactions = new ArrayList<>();

        // 아이템 정보를 미리 로드
        List<Item> items = itemRepository.findAll();
        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : items) {
            itemMap.put(item.getId(), item);
        }

        for (Row row : transactionSheet) {
            if (row.getRowNum() == 0) continue; // 헤더 스킵

            String transactionId = getStringCellValue(row.getCell(0));
            String itemId = getStringCellValue(row.getCell(1));
            LocalDate transactionDate = parseDate(row.getCell(2));
            String transactionType = getStringCellValue(row.getCell(3));
            
            // 수량 및 총 가격 처리
            Double quantityDouble = getNumericCellValue(row.getCell(4)); // purchase_quantity 또는 sell_quantity
            Integer quantity = (quantityDouble != null) ? quantityDouble.intValue() : null; // null 체크 후 int 변환

            // 총 가격 처리
            Double totalPrice = getNumericCellValue(row.getCell(5)); // 총 가격 (total_price)

            if (itemId == null || transactionDate == null || transactionType == null || quantity == null) {
                logger.warn("Skipping row {}: Missing required transaction data", row.getRowNum());
                continue;
            }

            try {
                Item item = itemMap.get(itemId);
                if (item == null) {
                    logger.warn("Item with ID {} not found. Skipping row {}", itemId, row.getRowNum());
                    continue;
                }

                TransactionRecord transaction = new TransactionRecord();

                // 유일한 transaction_id가 필요하다면 다음과 같이 생성
                if (transactionId == null || transactionRecordRepository.existsById(transactionId)) {
                    transactionId = "tr_" + UUID.randomUUID().toString(); // 새로운 ID 생성
                }

                transaction.setTransactionId(transactionId);
                transaction.setItem(item);
                transaction.setTransactionDate(transactionDate);
                transaction.setTransactionType(transactionType);
                
                // 거래 유형에 따라 가격 설정
                if ("purchase".equalsIgnoreCase(transactionType)) {
                    transaction.setPurchasePrice(item.getPurchasePrice());
                    transaction.setPurchaseQuantity(quantity);
                    totalPrice = item.getPurchasePrice() * quantity; // 구매 가격 곱하기 수량
                } else if ("sale".equalsIgnoreCase(transactionType)) {
                    transaction.setSellPrice(item.getSellPrice());
                    transaction.setSellQuantity(quantity);
                    totalPrice = item.getSellPrice() * quantity; // 판매 가격 곱하기 수량
                } else {
                    logger.warn("Unknown transaction type '{}' in row {}", transactionType, row.getRowNum());
                    continue;
                }

                transaction.setTotalPrice(totalPrice);
                transactions.add(transaction);
            } catch (Exception e) {
                logger.warn("Error parsing transaction row: {} at row {}", transactionId, row.getRowNum(), e);
            }
        }

        if (!transactions.isEmpty()) {
            transactionRecordRepository.saveAll(transactions);
            logger.info("Saved {} transactions to the database.", transactions.size());
        } else {
            logger.warn("No transactions to save.");
        }
    }


    private LocalDate parseDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else {
            try {
                return LocalDate.parse(getStringCellValue(cell), DATE_FORMATTER);
            } catch (Exception e) {
                logger.warn("Invalid date format: {}", cell);
                return null;
            }
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private Double getNumericCellValue(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : null;
    }
}
