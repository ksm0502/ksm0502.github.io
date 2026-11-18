package com.teamketo.shop.controller;

import com.teamketo.shop.dto.OrderResponseDto;
import com.teamketo.shop.entity.OrderStatus;
import com.teamketo.shop.service.MemberService;
import com.teamketo.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final MemberService memberService;

    // 주문 생성 (일반회원)
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody Map<String, Object> body, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        List<Long> productIds = readIntegerList(body.get("productIds"), "productIds", Long::valueOf);
        List<Integer> quantities = readIntegerList(body.get("quantities"), "quantities", Integer::valueOf);
        String deliveryAddress = readDeliveryField(body, "deliveryAddress");
        String deliveryReceiver = readDeliveryField(body, "deliveryReceiver");
        String deliveryPhone = readDeliveryField(body, "deliveryPhone");

        return ResponseEntity.ok(new OrderResponseDto(orderService.createOrder(
                memberId, productIds, quantities,
                deliveryAddress, deliveryReceiver, deliveryPhone)));
    }

    // 내 주문 목록 (일반회원)
    @GetMapping("/my/{memberId}")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(@PathVariable Long memberId, Principal principal) {
        Long currentMemberId = memberService.requireOwnMember(principal.getName(), memberId).getId();
        return ResponseEntity.ok(orderService.getMyOrders(currentMemberId).stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList()));
    }

    // 주문 상세 (일반회원)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        return ResponseEntity.ok(new OrderResponseDto(orderService.getOrder(orderId, memberId)));
    }

    // 주문 취소 (일반회원)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
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

    private static <T extends Number> List<T> readIntegerList(
            Object value, String field, Function<String, T> parse) {
        if (!(value instanceof List<?> items)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + "는 필수 배열입니다.");
        }
        try {
            return items.stream().map(item -> parse.apply(String.valueOf(item))).toList();
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + "에는 정수만 입력할 수 있습니다.");
        }
    }

    private static String readDeliveryField(Map<String, Object> body, String field) {
        if (!(body.get(field) instanceof String value) || value.isBlank() || value.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + "에 1~255자의 값을 입력해 주세요.");
        }
        return value;
    }
}
