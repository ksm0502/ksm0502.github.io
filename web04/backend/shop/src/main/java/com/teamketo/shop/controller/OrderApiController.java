package com.teamketo.shop.controller;

import com.teamketo.shop.dto.OrderResponseDto;
import com.teamketo.shop.entity.OrderStatus;
import com.teamketo.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    // 주문 생성 (일반회원)
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        List<Long> productIds = ((List<?>) body.get("productIds"))
                .stream().map(id -> Long.valueOf(id.toString())).toList();
        List<Integer> quantities = ((List<?>) body.get("quantities"))
                .stream().map(q -> Integer.valueOf(q.toString())).toList();
        String deliveryAddress = body.get("deliveryAddress").toString();
        String deliveryReceiver = body.get("deliveryReceiver").toString();
        String deliveryPhone = body.get("deliveryPhone").toString();

        return ResponseEntity.ok(new OrderResponseDto(orderService.createOrder(
                memberId, productIds, quantities,
                deliveryAddress, deliveryReceiver, deliveryPhone)));
    }

    // 내 주문 목록 (일반회원)
    @GetMapping("/my/{memberId}")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(@PathVariable Long memberId) {
        return ResponseEntity.ok(orderService.getMyOrders(memberId).stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList()));
    }

    // 주문 상세 (일반회원)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(new OrderResponseDto(orderService.getOrder(orderId)));
    }

    // 주문 취소 (일반회원)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId,
                                            @RequestParam Long memberId) {
        orderService.cancelOrder(orderId, memberId);
        return ResponseEntity.ok().build();
    }

    // 전체 주문 목록 (관리자)
    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders().stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList()));
    }

    // 주문 상태 변경 (관리자)
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long orderId,
                                             @RequestParam OrderStatus status) {
        orderService.changeOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
