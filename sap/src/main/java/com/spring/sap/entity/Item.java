package com.spring.sap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @Column(name = "item_id")
    private String id; // String으로 ID 정의

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
        // ID는 외부에서 설정되므로 기본 생성자는 비워두어도 됨
    }
}
