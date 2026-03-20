package com.teamketo.shop.controller;

import com.teamketo.shop.entity.CartItem;
import com.teamketo.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        return ResponseEntity.ok(cartService.addToCart(memberId, productId, quantity));
    }

    // 장바구니 목록 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long memberId) {
        return ResponseEntity.ok(cartService.getCartItems(memberId));
    }

    // 수량 변경
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long cartItemId,
                                                   @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(cartItemId, quantity));
    }

    // 단건 삭제
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok().build();
    }

    // 전체 삭제
    @DeleteMapping("/clear/{memberId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long memberId) {
        cartService.clearCart(memberId);
        return ResponseEntity.ok().build();
    }
}
