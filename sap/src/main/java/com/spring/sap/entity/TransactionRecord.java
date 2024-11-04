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

    /**
     * 판매와 구매의 차익(수익)을 계산하는 메서드
     * 판매가격과 수량이 null이 아닐 경우, 판매 총액을 계산
     * 구매가격과 수량이 null이 아닐 경우, 구매 총액을 계산
     * 최종적으로 판매 총액에서 구매 총액을 빼서 반환
     * 
     * @return 판매 수익 (총 판매 금액 - 총 구매 금액)
     */
    public Double getProfit() {
        double totalSellPrice = (sellPrice != null && sellQuantity != null) ? sellPrice * sellQuantity : 0.0;
        double totalPurchasePrice = (purchasePrice != null && purchaseQuantity != null) ? purchasePrice * purchaseQuantity : 0.0;
        return totalSellPrice - totalPurchasePrice;
    }

    /**
     * 총 거래 금액을 계산하는 메서드
     * 거래 유형이 "purchase"인 경우 구매 가격과 수량으로 총 가격을 계산
     * 거래 유형이 "sale"인 경우 판매 가격과 수량으로 총 가격을 계산
     * 그 외의 경우 총 가격은 0으로 설정
     */
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
