package com.teamketo.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer level;

    private Long parentId;

    // 상품 상세 페이지용 (null이면 전체 Q&A 게시판)
    private Long productId;

    private String title;

    private String content;

    private String writer;

    @Builder.Default
    private Integer viewCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
