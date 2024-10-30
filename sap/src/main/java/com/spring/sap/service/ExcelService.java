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

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    // 엑셀 파일에서 아이템 데이터를 저장하는 메서드
    public void saveItemsFromExcel(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            // 첫 번째 시트는 아이템 정보가 있는 시트로 가정합니다
            Sheet itemSheet = workbook.getSheetAt(0);
            List<Item> items = new ArrayList<>();

            for (Row row : itemSheet) {
                if (row.getRowNum() == 0) continue; // 헤더 스킵

                String itemIdString = getStringCellValue(row.getCell(0)); // item_id
                String name = getStringCellValue(row.getCell(1));   // 제품명
                String parts = getStringCellValue(row.getCell(2));  // 부품명
                String maker = getStringCellValue(row.getCell(3));  // 제조사
                Double purchasePrice = getNumericCellValue(row.getCell(4)); // 매입 가격
                Double sellPrice = parsePrice(getStringCellValue(row.getCell(5))); // 판매 가격 (쉼표 제거)
                String performance = getStringCellValue(row.getCell(6));    // 성능

                try {
                    Long itemId = Long.parseLong(itemIdString.replaceAll("[^\\d]", ""));
                    // 중복 검사 및 저장
                    if (!itemRepository.existsById(itemId)) {
                        Item item = new Item();
                        item.setId(itemId);
                        item.setName(name);
                        item.setParts(parts);
                        item.setMaker(maker);
                        item.setPurchasePrice(purchasePrice);
                        item.setSellPrice(sellPrice);
                        item.setPerformance(performance);

                        items.add(item);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid item ID format: " + itemIdString);
                }
            }

            itemRepository.saveAll(items); // 중복되지 않은 항목들만 저장
        } catch (Exception e) {
            logger.error("Error while processing Excel file", e);
        }
    }

    // 엑셀 파일에서 거래 데이터를 저장하는 메서드
    public void saveTransactionsFromExcel(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            // 두 번째 시트는 거래 정보가 있는 시트로 가정합니다
            Sheet transactionSheet = workbook.getSheetAt(1);
            List<TransactionRecord> transactions = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Row row : transactionSheet) {
                if (row.getRowNum() == 0) continue; // 헤더 스킵

                String transactionIdString = getStringCellValue(row.getCell(0)); // transaction_id
                String itemIdString = getStringCellValue(row.getCell(1));        // item_id
                String transactionDateString = getStringCellValue(row.getCell(2)); // 거래 날짜
                String transactionType = getStringCellValue(row.getCell(3));      // 거래 유형
                Double quantityDouble = getNumericCellValue(row.getCell(4));      // 거래 수량
                Integer quantity = (quantityDouble != null) ? quantityDouble.intValue() : 0;
                Double totalPrice = getNumericCellValue(row.getCell(5));          // 총 거래 가격

                try {
                    Long itemId = Long.parseLong(itemIdString.replaceAll("[^\\d]", ""));
                    LocalDate transactionDate = LocalDate.parse(transactionDateString, formatter);

                    // 해당 itemId에 대한 Item을 조회
                    Item item = itemRepository.findById(itemId).orElse(null);
                    if (item == null) continue; // item이 없는 경우 스킵

                    // 새로운 TransactionRecord 객체 생성 및 설정
                    TransactionRecord transaction = new TransactionRecord();
                    transaction.setTransactionId(transactionIdString);
                    transaction.setItem(item);
                    transaction.setTransactionDate(transactionDate);
                    transaction.setTransactionType(transactionType);
                    transaction.setQuantity(quantity);
                    transaction.setTotalPrice(totalPrice);

                    transactions.add(transaction);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid ID format: transactionId=" + transactionIdString + ", itemId=" + itemIdString);
                } catch (Exception e) {
                    logger.warn("Error parsing transaction row", e);
                }
            }

            transactionRecordRepository.saveAll(transactions); // 모든 거래 항목 저장
        } catch (Exception e) {
            logger.error("Error while processing Excel file", e);
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> null;
        };
    }

    private Double getNumericCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            return null;
        }
    }

    // 판매 가격 처리 (쉼표 제거 및 숫자 변환)
    private Double parsePrice(String priceString) {
        if (priceString == null) return null;
        try {
            return Double.parseDouble(priceString.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            logger.warn("Invalid price format: " + priceString);
            return null;
        }
    }
}
