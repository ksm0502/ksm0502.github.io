package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.OrderRepository;
import com.teamketo.shop.repository.ProductRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderValidationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;

    private Member customer;
    private Product product;

    @BeforeEach
    void createOrderFixtures() {
        customer = memberRepository.save(Member.builder().name("Order test customer")
                .email(UUID.randomUUID() + "@example.test")
                .password("unused-by-mock-session").role(Role.USER).build());
        product = productRepository.save(Product.builder().name("Order test product")
                .price(10_000).stock(10).categoryId(1L).build());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"productIds\":[],\"quantities\":[]",
            "\"productIds\":[%1$d]",
            "\"productIds\":[%1$d],\"quantities\":2",
            "\"productIds\":[%1$d,%1$d],\"quantities\":[1]",
            "\"productIds\":[%1$d],\"quantities\":[1,2]",
            "\"productIds\":[0],\"quantities\":[1]",
            "\"productIds\":[%1$d],\"quantities\":[0]",
            "\"productIds\":[%1$d],\"quantities\":[-1]",
            "\"productIds\":[%1$d],\"quantities\":[null]",
            "\"productIds\":[%1$d],\"quantities\":[1.5]",
            "\"productIds\":[%1$d],\"quantities\":[\"abc\"]",
            "\"productIds\":[%1$d],\"quantities\":[2147483648]"
    })
    void invalidItemsReturnBadRequestWithoutChangingData(String orderItems) throws Exception {
        submitOrder(orderItems.formatted(product.getId()), "QA address")
                .andExpect(status().isBadRequest());
        assertOrderAndStockUnchanged();
    }

    @Test
    void blankDeliveryAddressReturnsBadRequest() throws Exception {
        submitOrder("\"productIds\":[%d],\"quantities\":[1]".formatted(product.getId()), " ")
                .andExpect(status().isBadRequest());
        assertOrderAndStockUnchanged();
    }

    @Test
    void insufficientStockReturnsConflictWithoutChangingData() throws Exception {
        submitOrder("\"productIds\":[%d],\"quantities\":[11]".formatted(product.getId()), "QA address")
                .andExpect(status().isConflict());
        assertOrderAndStockUnchanged();
    }

    @Test
    void missingProductReturnsNotFoundWithoutCreatingAnOrder() throws Exception {
        submitOrder("\"productIds\":[9223372036854775807],\"quantities\":[1]", "QA address")
                .andExpect(status().isNotFound());
        assertOrderAndStockUnchanged();
    }

    @Test
    void oversizedOrderTotalReturnsBadRequestWithoutChangingData() throws Exception {
        product.setPrice(Integer.MAX_VALUE);
        submitOrder("\"productIds\":[%d],\"quantities\":[2]".formatted(product.getId()), "QA address")
                .andExpect(status().isBadRequest());
        assertOrderAndStockUnchanged();
    }

    private ResultActions submitOrder(String items, String address) throws Exception {
        return mockMvc.perform(post("/api/orders").with(csrf()).with(user(customer.getEmail()).roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {%s,"deliveryAddress":"%s","deliveryReceiver":"QA customer",
                        "deliveryPhone":"010-0000-0000"}
                        """.formatted(items, address)));
    }

    private void assertOrderAndStockUnchanged() {
        assertThat(orderRepository.findByMemberIdOrderByCreatedAtDesc(customer.getId())).isEmpty();
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(10);
    }
}
