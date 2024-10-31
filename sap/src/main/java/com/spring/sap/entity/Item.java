package com.spring.sap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @Column(name = "item_id")
    private String id = UUID.randomUUID().toString(); // UUID로 ID 초기화

    @Column(nullable = false)
    private String name;

    private String parts;
    private String maker;
    
    @Column(name = "purchase_price")
    private Double purchasePrice;
    
    @Column(name = "sell_price")
    private Double sellPrice;

    private String performance;

    // 기본 생성자
    public Item() {
        this.id = UUID.randomUUID().toString(); // 객체 생성 시 고유 ID 부여
    }
}
