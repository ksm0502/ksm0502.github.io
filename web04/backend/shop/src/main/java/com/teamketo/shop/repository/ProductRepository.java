package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품명으로 조회한 상품 목록
    List<Product> findByNameContaining(String name);

    // 카테고리로 조회되는 상품 목록
    List<Product> findByCategoryId(Long categoryId);

    // 메인 페이지용 신상품 4건 (등록일 역순)
    List<Product> findTop4ByOrderByCreatedAtDesc();

    // 메인 페이지용 베스트 4건 (재고 적은 순 = 많이 팔린 순)
    List<Product> findTop4ByOrderByStockAsc();

    // 메인 페이지용 추천 상품 4건 (카테고리 1번 기준)
    List<Product> findTop4ByCategoryIdOrderByCreatedAtDesc(Long categoryId);
}