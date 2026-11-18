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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        // 여러 상품을 구매해도 각 거래가 상품 ID 순서로 잠금을 획득한다.
        Map<Long, Product> products = indexProducts(productRepository.findForStockUpdate(productIds));

        for (int i = 0; i < productIds.size(); i++) {
            Product product = products.get(productIds.get(i));
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
            }

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
                .orElseThrow(OrderService::orderNotFound);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = orderRepository.findByIdAndMemberIdForUpdate(orderId, memberId)
                .orElseThrow(OrderService::orderNotFound);
        cancelAndRestoreStock(order);
    }

    private void cancelAndRestoreStock(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "배송 중이거나 완료된 주문은 취소할 수 없습니다.");
        }

        Map<Long, Product> products = indexProducts(productRepository.findForOrderStockUpdate(order.getId()));
        for (OrderItem item : order.getOrderItems()) {
            Product product = products.get(item.getProduct().getId());
            if (product.getStock() != null) {
                try {
                    product.setStock(Math.addExact(product.getStock(), item.getQuantity()));
                } catch (ArithmeticException ex) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "복구할 재고가 허용 범위를 벗어났습니다.");
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    // 전체 주문 목록 (관리자)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // 주문 상태 변경 (관리자)
    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(OrderService::orderNotFound);
        if (status == OrderStatus.CANCELLED) {
            cancelAndRestoreStock(order);
            return;
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "취소된 주문은 상태를 변경할 수 없습니다.");
        }
        order.setStatus(status);
    }

    private static Map<Long, Product> indexProducts(List<Product> products) {
        return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private static ResponseStatusException orderNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
    }
}
