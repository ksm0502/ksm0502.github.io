package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Product;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    // 1. 전체 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // 2. 상품 상세 정보
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다.")));
    }

    // 3. 카테고리별 상품 목록
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productRepository.findByCategoryId(categoryId));
    }

    // 4. 상품명 검색
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productRepository.findByNameContaining(name));
    }

    // 5. 메인 신상품 4건
    @GetMapping("/main/new")
    public ResponseEntity<List<Product>> getNewArrivals() {
        return ResponseEntity.ok(productRepository.findTop4ByOrderByCreatedAtDesc());
    }

    // 6. 메인 베스트 4건 (재고 적은 순 = 많이 팔린 순)
    @GetMapping("/main/best")
    public ResponseEntity<List<Product>> getBestProducts() {
        return ResponseEntity.ok(productRepository.findTop4ByOrderByStockAsc());
    }

    // 7. 메인 추천 상품 4건 (도시락 골라담기 카테고리 기준)
    @GetMapping("/main/recommend")
    public ResponseEntity<List<Product>> getRecommendProducts() {
        return ResponseEntity.ok(productRepository.findTop4ByCategoryIdOrderByCreatedAtDesc(1L));
    }

    // 8. [관리자] 상품 등록
    @PostMapping("/admin")
    public ResponseEntity<Product> registerProduct(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(productService.insertProduct(product, image));
    }

    // 9. [관리자] 상품 수정
    @PutMapping("/admin/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(productService.updateProduct(id, product, image));
    }

    // 10. [관리자] 상품 삭제
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}