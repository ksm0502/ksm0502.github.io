package com.teamketo.shop.repository;

import com.teamketo.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 회원의 장바구니 목록
    List<CartItem> findByMemberId(Long memberId);

    // 특정 회원의 특정 상품 (중복 담기 방지용)
    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);

    // 특정 회원의 장바구니 전체 삭제 (주문 완료 후)
    void deleteByMemberId(Long memberId);
}
