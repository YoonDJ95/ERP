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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id는 자동 증가
    private Long id;

    @Column(unique = true)
    private String transactionId;

    @PostPersist
    public void generateTransactionId() {
        if (transactionId == null) {
            transactionId = "tr_" + String.format("%05d", id);
        }
    }

    // Item과의 다대일 관계를 정의하며, 필수 관계임
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // 거래 날짜를 저장
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    // 거래 유형 (예: purchase, sale)을 저장
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    // 구매 단가를 저장 (optional)
    @Column(name = "purchase_price")
    private Double purchasePrice;

    // 구매 수량을 저장 (optional)
    @Column(name = "purchase_quantity")
    private Integer purchaseQuantity;

    // 판매 단가를 저장 (optional)
    @Column(name = "sell_price")
    private Double sellPrice;

    // 판매 수량을 저장 (optional)
    @Column(name = "sell_quantity")
    private Integer sellQuantity;

    // 총 거래 금액을 저장
    @Column(name = "total_price")
    private Double totalPrice;

    // 수익을 저장
    @Column(name = "profit")
    private Double profit;

    // Entity가 persist되거나 update될 때 총 가격과 수익을 계산
    @PrePersist
    @PreUpdate
    public void calculateValues() {
        calculateTotalPrice();
        calculateProfit();
    }

    /**
     * 총 거래 금액을 계산하는 메서드
     */
    public void calculateTotalPrice() {
        if ("purchase".equalsIgnoreCase(transactionType) && purchasePrice != null && purchaseQuantity != null) {
            this.totalPrice = purchasePrice * purchaseQuantity;
        } else if ("sale".equalsIgnoreCase(transactionType) && sellPrice != null && sellQuantity != null) {
            this.totalPrice = sellPrice * sellQuantity;
        } else {
            this.totalPrice = 0.0;
        }
    }

    /**
     * 수익을 계산하는 메서드
     */
    public void calculateProfit() {
        if ("sale".equalsIgnoreCase(transactionType)) {
            // 판매 거래인 경우: 총 판매 금액 - 총 구매 금액
            double totalSellPrice = (sellPrice != null && sellQuantity != null) ? sellPrice * sellQuantity : 0.0;
            double totalPurchasePrice = (purchasePrice != null && purchaseQuantity != null) ? purchasePrice * purchaseQuantity : 0.0;
            this.profit = totalSellPrice - totalPurchasePrice;
        } else {
            // 구매 거래인 경우 수익은 발생하지 않음
            this.profit = 0.0;
        }
    }
}
