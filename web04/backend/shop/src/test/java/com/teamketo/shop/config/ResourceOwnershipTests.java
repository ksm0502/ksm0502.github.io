package com.teamketo.shop.config;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.OrderStatus;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.CartItemRepository;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.OrderRepository;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.service.CartService;
import com.teamketo.shop.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ResourceOwnershipTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderService orderService;
    @Autowired private CartService cartService;
    @Autowired private PasswordEncoder passwordEncoder;

    private Member customer;
    private Member otherCustomer;
    private RequestPostProcessor customerSession;

    @BeforeEach
    void createMembers() {
        customer = createMember("Customer");
        otherCustomer = createMember("Other customer");
        customerSession = user(customer.getEmail()).roles("USER");
    }

    @Test
    void memberCanOnlyReadAndModifyTheirOwnAccount() throws Exception {
        String update = "{\"name\":\"Updated customer\"}";

        mockMvc.perform(get("/{email}", otherCustomer.getEmail()).with(customerSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/{id}", otherCustomer.getId()).with(customerSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/{id}/password", otherCustomer.getId()).with(customerSession)
                        .param("password", "updated-test-password"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/{id}", otherCustomer.getId()).with(customerSession))
                .andExpect(status().isForbidden());
        Member unchanged = memberRepository.findById(otherCustomer.getId()).orElseThrow();
        assertThat(unchanged.getName()).isEqualTo("Other customer");
        assertThat(passwordEncoder.matches("test-password", unchanged.getPassword())).isTrue();

        mockMvc.perform(get("/{email}", customer.getEmail()).with(customerSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(customer.getEmail()));
        mockMvc.perform(put("/{id}", customer.getId()).with(customerSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk());
        mockMvc.perform(put("/{id}/password", customer.getId()).with(customerSession)
                        .param("password", "updated-test-password"))
                .andExpect(status().isOk());
        Member updated = memberRepository.findById(customer.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated customer");
        assertThat(passwordEncoder.matches("updated-test-password", updated.getPassword())).isTrue();
        mockMvc.perform(delete("/{id}", customer.getId()).with(customerSession))
                .andExpect(status().isOk());
        assertThat(memberRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void ordersUseTheAuthenticatedCustomerAndProtectOtherOrders() throws Exception {
        Product product = createProduct();
        var otherOrder = orderService.createOrder(otherCustomer.getId(), List.of(product.getId()),
                List.of(1), "QA address", "Other customer", "010-0000-0000");

        mockMvc.perform(get("/api/orders/my/{id}", otherCustomer.getId()).with(customerSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/orders/{id}", otherOrder.getId()).with(customerSession))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/orders/{id}/cancel", otherOrder.getId()).with(customerSession)
                        .param("memberId", otherCustomer.getId().toString()))
                .andExpect(status().isNotFound());
        assertThat(orderRepository.findById(otherOrder.getId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.ORDER_COMPLETE);
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(9);

        mockMvc.perform(post("/api/orders").with(customerSession).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":%d,"productIds":[%d],"quantities":[2],
                                "deliveryAddress":"QA address","deliveryReceiver":"Customer",
                                "deliveryPhone":"010-0000-0000"}
                                """.formatted(otherCustomer.getId(), product.getId())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.memberEmail").value(customer.getEmail()));
        var ownOrder = orderRepository.findByMemberIdOrderByCreatedAtDesc(customer.getId()).getFirst();
        mockMvc.perform(get("/api/orders/my/{id}", customer.getId()).with(customerSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(ownOrder.getId().intValue()));
        mockMvc.perform(get("/api/orders/{id}", ownOrder.getId()).with(customerSession))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/orders/{id}/cancel", ownOrder.getId()).with(customerSession)
                        .param("memberId", customer.getId().toString()))
                .andExpect(status().isOk());
        assertThat(orderRepository.findById(ownOrder.getId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(9);
    }

    @Test
    void cartUsesTheAuthenticatedCustomerAndProtectsOtherItems() throws Exception {
        Product product = createProduct();
        var otherItem = cartService.addToCart(otherCustomer.getId(), product.getId(), 1);

        mockMvc.perform(get("/api/cart/{id}", otherCustomer.getId()).with(customerSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/cart/clear/{id}", otherCustomer.getId()).with(customerSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/cart/{id}", otherItem.getId()).with(customerSession).param("quantity", "2"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/cart/{id}", otherItem.getId()).with(customerSession))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/cart").with(customerSession).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":%d,"productId":%d,"quantity":2}
                                """.formatted(otherCustomer.getId(), product.getId())))
                .andExpect(status().isOk());
        var ownItem = cartItemRepository.findByMemberIdAndProductId(customer.getId(), product.getId()).orElseThrow();
        mockMvc.perform(get("/api/cart/{id}", customer.getId()).with(customerSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].quantity").value(2));
        mockMvc.perform(put("/api/cart/{id}", ownItem.getId()).with(customerSession).param("quantity", "3"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(3));
        mockMvc.perform(delete("/api/cart/{id}", ownItem.getId()).with(customerSession))
                .andExpect(status().isOk());
        assertThat(cartItemRepository.findByMemberId(customer.getId())).isEmpty();

        cartService.addToCart(customer.getId(), product.getId(), 1);
        mockMvc.perform(delete("/api/cart/clear/{id}", customer.getId()).with(customerSession))
                .andExpect(status().isOk());
        assertThat(cartItemRepository.findByMemberId(customer.getId())).isEmpty();
        assertThat(cartItemRepository.findById(otherItem.getId()).orElseThrow().getQuantity()).isEqualTo(1);
    }

    private Member createMember(String name) {
        return memberRepository.save(Member.builder().name(name)
                .email(UUID.randomUUID() + "@example.test")
                .password(passwordEncoder.encode("test-password"))
                .role(Role.USER).build());
    }

    private Product createProduct() {
        return productRepository.save(Product.builder().name("Ownership test")
                .price(10_000).stock(10).categoryId(1L).build());
    }
}
