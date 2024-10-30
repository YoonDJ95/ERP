package com.spring.sap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") // Hibernate가 item_id로 인식하도록 명시
    private Long id;

    @Column(nullable = false)
    private String name;

    private String parts;
    private String maker;
    private Double purchasePrice;
    private Double sellPrice;
    private String performance;
}
