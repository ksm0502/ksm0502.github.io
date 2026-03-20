package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Review;
import com.teamketo.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer rating = Integer.valueOf(body.get("rating").toString());
        String content = body.get("content").toString();
        return ResponseEntity.ok(reviewService.createReview(memberId, productId, rating, content));
    }

    // 특정 상품 리뷰 목록
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // 내 리뷰 목록 (마이페이지)
    @GetMapping("/my/{memberId}")
    public ResponseEntity<List<Review>> getMyReviews(@PathVariable Long memberId) {
        return ResponseEntity.ok(reviewService.getMyReviews(memberId));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId,
                                               @RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        Integer rating = Integer.valueOf(body.get("rating").toString());
        String content = body.get("content").toString();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, memberId, rating, content));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @RequestParam Long memberId,
                                             @RequestParam(defaultValue = "false") boolean isAdmin) {
        reviewService.deleteReview(reviewId, memberId, isAdmin);
        return ResponseEntity.ok().build();
    }

    // 전체 리뷰 목록 (관리자)
    @GetMapping("/admin")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}