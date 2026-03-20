package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 상품의 리뷰 목록 (최신순)
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    // 특정 회원이 특정 상품에 리뷰 작성했는지 확인
    boolean existsByProductIdAndMemberId(Long productId, Long memberId);

    // 특정 회원의 리뷰 목록 (마이페이지용)
    List<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 관리자용 전체 리뷰 목록
    List<Review> findAllByOrderByCreatedAtDesc();
}
