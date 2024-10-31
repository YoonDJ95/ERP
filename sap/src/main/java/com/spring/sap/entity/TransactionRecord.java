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
    @JoinColumn(name = "item_id", nullable = false)  // 외래 키 매핑
    private Item item;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_price")
    private Double totalPrice;

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

    // 총 거래 가격 계산 메서드
    public Double calculateTotalPrice() {
        this.totalPrice = (price != null && quantity != null) ? price * quantity : 0.0;
        return this.totalPrice;
    }

    // 수익 계산 메서드 (판매 수익 - 매입 비용)
    public Double getProfit() {
        if (sellPrice != null && sellQuantity != null && purchasePrice != null && purchaseQuantity != null) {
            return (sellPrice * sellQuantity) - (purchasePrice * purchaseQuantity);
        }
        return 0.0;
    }
}
