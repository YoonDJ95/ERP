package com.spring.sap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "transaction_identifier", unique = true, nullable = false)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    // 매입 정보 필드들
    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "purchase_quantity")
    private Integer purchaseQuantity;

    // 판매 정보 필드들
    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "sell_quantity")
    private Integer sellQuantity;

    @Column(name = "total_price")
    private Double totalPrice;

    // 수익 계산 메서드
    public Double getProfit() {
        double totalSellPrice = (sellPrice != null && sellQuantity != null) ? sellPrice * sellQuantity : 0.0;
        double totalPurchasePrice = (purchasePrice != null && purchaseQuantity != null) ? purchasePrice * purchaseQuantity : 0.0;
        return totalSellPrice - totalPurchasePrice;
    }

    // 총 거래 가격 계산 메서드
    public void calculateTotalPrice() {
        if ("purchase".equals(transactionType) && purchasePrice != null && purchaseQuantity != null) {
            this.totalPrice = purchasePrice * purchaseQuantity; // 매입 가격에 매입량 곱하기
        } else if ("sale".equals(transactionType) && sellPrice != null && sellQuantity != null) {
            this.totalPrice = sellPrice * sellQuantity; // 판매 가격에 판매량 곱하기
        } else {
            this.totalPrice = 0.0; // 조건에 맞지 않으면 0으로 설정
        }
    }
}
