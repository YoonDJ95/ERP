package com.spring.sap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Item 엔티티: 데이터베이스의 아이템 정보 테이블과 매핑
@Entity
@Getter
@Setter
public class Item {

    // 아이템 ID 필드, 기본키로 사용하며 String 타입으로 정의
    @Id
    @Column(name = "item_id") // DB의 컬럼명은 item_id
    private String id;

    // 아이템 이름 필드, null 허용 안 함
    @Column(nullable = false)
    private String name;

    // 부품명 필드
    private String parts;

    // 제조사 필드
    private String maker;
    
    // 구매 가격 필드, 컬럼명은 "purchase_price"로 지정
    @Column(name = "purchase_price")
    private Double purchasePrice;
    
    // 판매 가격 필드, 컬럼명은 "sell_price"로 지정
    @Column(name = "sell_price")
    private Double sellPrice;

    // 성능 필드, 아이템의 성능 정보 저장
    private String performance;

    // 기본 생성자, ID는 외부에서 설정되므로 비워둠
    public Item() {
    }
}
