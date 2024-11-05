package com.spring.sap.service;

import com.spring.sap.entity.Item;
import com.spring.sap.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * 필터링된 아이템 목록을 조회하는 메서드
     * @param name 조회할 아이템 이름 (null일 경우 모든 이름 포함)
     * @param parts 조회할 부품명 (null일 경우 모든 부품 포함)
     * @param maker 조회할 제조사 (null일 경우 모든 제조사 포함)
     * @param minPurchasePrice 최소 매입 가격 (null일 경우 제한 없음)
     * @param maxPurchasePrice 최대 매입 가격 (null일 경우 제한 없음)
     * @param minSellPrice 최소 판매 가격 (null일 경우 제한 없음)
     * @param maxSellPrice 최대 판매 가격 (null일 경우 제한 없음)
     * @param performance 조회할 성능 정보 (null일 경우 모든 성능 포함)
     * @return 필터 조건에 맞는 아이템 목록 반환
     */
    public List<Item> getFilteredItems(String name, String parts, String maker,
            Integer minPurchasePrice, Integer maxPurchasePrice,
            Integer minSellPrice, Integer maxSellPrice,
            String performance) {
// 빈 문자열을 null로 변환
if (name != null && name.trim().isEmpty()) name = null;
if (parts != null && parts.trim().isEmpty()) parts = null;
if (maker != null && maker.trim().isEmpty()) maker = null;
if (performance != null && performance.trim().isEmpty()) performance = null;

// 필터링 조건이 있을 때만 쿼리 실행
if (name == null && parts == null && maker == null &&
minPurchasePrice == null && maxPurchasePrice == null &&
minSellPrice == null && maxSellPrice == null &&
performance == null) {
return itemRepository.findAll();
}



// 필터 조건을 확인하는 로그 추가
System.out.println("Filtering with conditions:");
System.out.println("Name: " + name + ", Parts: " + parts + ", Maker: " + maker);
System.out.println("Min Purchase Price: " + minPurchasePrice + ", Max Purchase Price: " + maxPurchasePrice);
System.out.println("Min Sell Price: " + minSellPrice + ", Max Sell Price: " + maxSellPrice);
System.out.println("Performance: " + performance);

// 필터 적용된 결과 반환
return itemRepository.findFilteredItems(name, parts, maker, minPurchasePrice, maxPurchasePrice, minSellPrice, maxSellPrice, performance);
}

}
