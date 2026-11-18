package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Order;
import com.teamketo.shop.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndMemberId(Long id, Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id and o.member.id = :memberId")
    Optional<Order> findByIdAndMemberIdForUpdate(@Param("id") Long id, @Param("memberId") Long memberId);

    // 특정 회원의 주문 목록 (최신순)
    List<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 특정 회원의 특정 상태 주문 목록
    List<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status);

    // 전체 주문 목록 (관리자용, 최신순)
    List<Order> findAllByOrderByCreatedAtDesc();

    // 상태별 주문 목록 (관리자용)
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
