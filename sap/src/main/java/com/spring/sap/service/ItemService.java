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
     * 전체 아이템 목록을 반환하는 메서드
     * @return 전체 Item 목록
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
