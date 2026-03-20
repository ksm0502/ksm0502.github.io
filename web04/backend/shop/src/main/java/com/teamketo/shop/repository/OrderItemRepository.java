package com.teamketo.shop.repository;

import com.teamketo.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 특정 주문의 주문 상품 목록
    List<OrderItem> findByOrderId(Long orderId);

    // 특정 상품의 주문 목록 (재고 관리용)
    List<OrderItem> findByProductId(Long productId);
}
