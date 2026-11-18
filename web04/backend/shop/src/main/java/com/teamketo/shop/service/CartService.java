package com.teamketo.shop.service;

import com.teamketo.shop.entity.CartItem;
import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.repository.CartItemRepository;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 장바구니 담기 (이미 있으면 수량 추가)
    @Transactional
    public CartItem addToCart(Long memberId, Long productId, Integer quantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 이미 담긴 상품이면 수량만 추가
        return cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .map(item -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    return cartItemRepository.save(item);
                })
                .orElseGet(() -> cartItemRepository.save(
                        CartItem.builder()
                                .member(member)
                                .product(product)
                                .quantity(quantity)
                                .build()
                ));
    }

    // 장바구니 목록 조회
    public List<CartItem> getCartItems(Long memberId) {
        return cartItemRepository.findByMemberId(memberId);
    }

    // 수량 변경
    @Transactional
    public CartItem updateQuantity(Long cartItemId, Long memberId, Integer quantity) {
        CartItem item = getCartItem(cartItemId, memberId);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    // 장바구니 단건 삭제
    @Transactional
    public void deleteCartItem(Long cartItemId, Long memberId) {
        cartItemRepository.delete(getCartItem(cartItemId, memberId));
    }

    // 장바구니 전체 삭제 (주문 완료 후)
    @Transactional
    public void clearCart(Long memberId) {
        cartItemRepository.deleteByMemberId(memberId);
    }

    private CartItem getCartItem(Long cartItemId, Long memberId) {
        return cartItemRepository.findByIdAndMemberId(cartItemId, memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니 항목을 찾을 수 없습니다."));
    }
}
