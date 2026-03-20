package com.teamketo.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer stock; // 재고 관리 필수 (주문 시 차감 로직용)

    private String imageUrl; // FileUploadUtil이 반환한 파일명 저장

    private Long categoryId; // 카테고리별 분류용

    private LocalDateTime createdAt; // 신상품(최신순) 정렬용

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}