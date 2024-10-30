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
    
    
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;  // 거래 유형 (구매, 판매)

    @Column(name = "price")
    private Double price;  // 거래 단가

    @Column(name = "quantity")
    private Integer quantity;  // 거래 수량

    @Column(name = "total_price")
    private Double totalPrice;  // 총 거래 가격

    @Column(name = "purchase_price")
    private Double purchasePrice;  // 매입 가격

    @Column(name = "sell_price")
    private Double sellPrice;  // 판매 가격

    @Column(name = "purchase_quantity")
    private Integer purchaseQuantity;  // 매입 수량

    @Column(name = "sell_quantity")
    private Integer sellQuantity;  // 판매 수량

    // 총 거래 가격 계산 메서드
    public void calculateTotalPrice() {
        this.totalPrice = (price != null && quantity != null) ? price * quantity : 0.0;
    }

    // 수익 계산 메서드 (판매 수익 - 매입 비용)
    public Double getProfit() {
        if (sellPrice != null && sellQuantity != null && purchasePrice != null && purchaseQuantity != null) {
            return (sellPrice * sellQuantity) - (purchasePrice * purchaseQuantity);
        } else {
            return 0.0;  // 필요한 값이 없을 경우 0.0으로 반환
        }
    }
}
