package com.teamketo.shop.service;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.entity.Review;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.OrderItemRepository;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    // 리뷰 등록 (구매자만 가능)
    @Transactional
    public Review createReview(Long memberId, Long productId, Integer rating, String content) {

        // 구매 여부 확인
        boolean hasPurchased = orderItemRepository.findByProductId(productId)
                .stream()
                .anyMatch(item -> item.getOrder().getMember().getId().equals(memberId));
        if (!hasPurchased) {
            throw new RuntimeException("구매한 상품에만 리뷰를 작성할 수 있습니다.");
        }

        // 중복 리뷰 확인
        if (reviewRepository.existsByProductIdAndMemberId(productId, memberId)) {
            throw new RuntimeException("이미 리뷰를 작성하셨습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        return reviewRepository.save(Review.builder()
                .member(member)
                .product(product)
                .rating(rating)
                .content(content)
                .build());
    }

    // 특정 상품 리뷰 목록
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    // 내 리뷰 목록 (마이페이지)
    public List<Review> getMyReviews(Long memberId) {
        return reviewRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 리뷰 수정
    @Transactional
    public Review updateReview(Long reviewId, Long memberId, Integer rating, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        if (!review.getMember().getId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        review.setRating(rating);
        review.setContent(content);
        return reviewRepository.save(review);
    }

    // 리뷰 삭제 (본인 또는 관리자)
    @Transactional
    public void deleteReview(Long reviewId, Long memberId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        if (!isAdmin && !review.getMember().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        reviewRepository.deleteById(reviewId);
    }

    // 전체 리뷰 목록 (관리자)
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }
}
