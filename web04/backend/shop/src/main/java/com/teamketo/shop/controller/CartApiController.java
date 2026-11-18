package com.teamketo.shop.controller;

import com.teamketo.shop.entity.CartItem;
import com.teamketo.shop.service.CartService;
import com.teamketo.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final MemberService memberService;

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody Map<String, Object> body, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        return ResponseEntity.ok(cartService.addToCart(memberId, productId, quantity));
    }

    // 장바구니 목록 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long memberId, Principal principal) {
        Long currentMemberId = memberService.requireOwnMember(principal.getName(), memberId).getId();
        return ResponseEntity.ok(cartService.getCartItems(currentMemberId));
    }

    // 수량 변경
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long cartItemId,
                                                   @RequestParam Integer quantity, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        return ResponseEntity.ok(cartService.updateQuantity(cartItemId, memberId, quantity));
    }

    // 단건 삭제
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId, Principal principal) {
        Long memberId = memberService.requireCurrentMember(principal.getName()).getId();
        cartService.deleteCartItem(cartItemId, memberId);
        return ResponseEntity.ok().build();
    }

    // 전체 삭제
    @DeleteMapping("/clear/{memberId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long memberId, Principal principal) {
        Long currentMemberId = memberService.requireOwnMember(principal.getName(), memberId).getId();
        cartService.clearCart(currentMemberId);
        return ResponseEntity.ok().build();
    }
}
