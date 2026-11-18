package com.teamketo.shop.service;

import com.teamketo.shop.entity.*;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.OrderRepository;
import com.teamketo.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 주문 생성
    @Transactional
    public Order createOrder(Long memberId, List<Long> productIds,
                             List<Integer> quantities,
                             String deliveryAddress,
                             String deliveryReceiver,
                             String deliveryPhone) {

        validateOrderItems(productIds, quantities);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Order order = Order.builder()
                .member(member)
                .deliveryAddress(deliveryAddress)
                .deliveryReceiver(deliveryReceiver)
                .deliveryPhone(deliveryPhone)
                .totalPrice(0)
                .build();

        long totalPrice = 0;

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

            int qty = quantities.get(i);

            // 재고 확인
            if (product.getStock() == null || product.getStock() < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "재고가 부족합니다: " + product.getName());
            }

            totalPrice += (long) product.getPrice() * qty;
            if (totalPrice > Integer.MAX_VALUE || totalPrice < Integer.MIN_VALUE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "주문 금액이 허용 범위를 벗어났습니다.");
            }

            // 재고 차감
            product.setStock(product.getStock() - qty);
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .price(product.getPrice())
                    .build();

            order.getOrderItems().add(item);
        }

        order.setTotalPrice((int) totalPrice);
        return orderRepository.save(order);
    }

    private static void validateOrderItems(List<Long> productIds, List<Integer> quantities) {
        if (productIds == null || quantities == null || productIds.isEmpty() || productIds.size() != quantities.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품과 수량을 같은 개수로 한 항목 이상 입력해 주세요.");
        }
        if (productIds.stream().anyMatch(id -> id == null || id <= 0)
                || quantities.stream().anyMatch(quantity -> quantity == null || quantity <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 ID와 수량은 양의 정수여야 합니다.");
        }
    }

    // 내 주문 목록 조회
    public List<Order> getMyOrders(Long memberId) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 주문 상세 조회
    public Order getOrder(Long orderId, Long memberId) {
        return orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = getOrder(orderId, memberId);

        // 배송중/배송완료는 취소 불가
        if (order.getStatus() == OrderStatus.SHIPPING ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("배송 중이거나 완료된 주문은 취소할 수 없습니다.");
        }

        // 재고 복구 (null 체크 추가)
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getStock() != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // 전체 주문 목록 (관리자)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // 주문 상태 변경 (관리자)
    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
