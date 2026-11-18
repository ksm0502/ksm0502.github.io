package com.teamketo.shop.controller;

import com.teamketo.shop.dto.PublicReviewResponse;
import com.teamketo.shop.entity.Review;
import com.teamketo.shop.service.MemberService;
import com.teamketo.shop.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> body, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer rating = Integer.valueOf(body.get("rating").toString());
        String content = body.get("content").toString();
        return ResponseEntity.ok(reviewService.createReview(memberId, productId, rating, content));
    }

    // 특정 상품 리뷰 목록
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PublicReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // 내 리뷰 목록 (마이페이지)
    @GetMapping("/my/{memberId}")
    public ResponseEntity<List<Review>> getMyReviews(@PathVariable Long memberId, Principal principal) {
        Long currentMemberId = memberService.requireOwnMember(principal.getName(), memberId).getId();
        return ResponseEntity.ok(reviewService.getMyReviews(currentMemberId));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId,
                                               @RequestBody Map<String, Object> body, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        Integer rating = Integer.valueOf(body.get("rating").toString());
        String content = body.get("content").toString();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, memberId, rating, content));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             Principal principal, HttpServletRequest request) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        reviewService.deleteReview(reviewId, memberId, request.isUserInRole("ADMIN"));
        return ResponseEntity.ok().build();
    }

    // 전체 리뷰 목록 (관리자)
    @GetMapping("/admin")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
