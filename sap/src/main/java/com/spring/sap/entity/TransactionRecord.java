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
    @Column(name = "transaction_id", nullable = false)
    private String transactionId; // String으로 유지

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "purchase_quantity")
    private Integer purchaseQuantity;

    @Column(name = "sell_price")
    private Double sellPrice;

    @Column(name = "sell_quantity")
    private Integer sellQuantity;

    @Column(name = "total_price")
    private Double totalPrice;

    public Double getProfit() {
        double totalSellPrice = (sellPrice != null && sellQuantity != null) ? sellPrice * sellQuantity : 0.0;
        double totalPurchasePrice = (purchasePrice != null && purchaseQuantity != null) ? purchasePrice * purchaseQuantity : 0.0;
        return totalSellPrice - totalPurchasePrice;
    }

    public void calculateTotalPrice() {
        if ("purchase".equals(transactionType) && purchasePrice != null && purchaseQuantity != null) {
            this.totalPrice = purchasePrice * purchaseQuantity;
        } else if ("sale".equals(transactionType) && sellPrice != null && sellQuantity != null) {
            this.totalPrice = sellPrice * sellQuantity;
        } else {
            this.totalPrice = 0.0;
        }
    }
}
