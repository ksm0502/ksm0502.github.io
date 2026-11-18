package com.teamketo.shop.config;

import com.teamketo.shop.controller.OrderApiController;
import com.teamketo.shop.controller.ProductController;
import com.teamketo.shop.controller.ReviewApiController;
import com.teamketo.shop.entity.Member;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.service.MemberService;
import com.teamketo.shop.service.OrderService;
import com.teamketo.shop.service.ProductService;
import com.teamketo.shop.service.ReviewService;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProductController.class, OrderApiController.class, ReviewApiController.class})
@Import(SecurityConfig.class)
class AdminApiSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private ReviewService reviewService;

    @ParameterizedTest(name = "{0}")
    @MethodSource("adminRequests")
    void adminApiRequiresAdminRole(String endpoint, Supplier<MockHttpServletRequestBuilder> request)
            throws Exception {
        mockMvc.perform(request.get())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(request.get().with(user("customer").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(request.get().with(user("manager").roles("MANAGER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(request.get().with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUsersCanBrowseProductsAndProductReviews() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/products/search").param("name", "coffee"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reviews/product/1"))
                .andExpect(status().isOk());
    }

    @Test
    void customerCanReadOrderAndReviewLists() throws Exception {
        when(memberService.requireOwnMember("customer", 1L)).thenReturn(Member.builder().id(1L).build());
        mockMvc.perform(get("/api/orders/my/1").with(user("customer").roles("USER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reviews/my/1").with(user("customer").roles("USER")))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> adminRequests() {
        return Stream.of(
                adminRequest("POST product", () -> multipart("/api/products/admin").file(productPart())),
                adminRequest("PUT product", () -> multipart(HttpMethod.PUT, "/api/products/admin/1").file(productPart())),
                adminRequest("DELETE product", () -> delete("/api/products/admin/1")),
                adminRequest("GET orders", () -> get("/api/orders/admin")),
                adminRequest("PUT order status", () -> put("/api/orders/admin/1/status").param("status", "READY")),
                adminRequest("GET reviews", () -> get("/api/reviews/admin"))
        );
    }

    private static Arguments adminRequest(String endpoint, Supplier<MockHttpServletRequestBuilder> request) {
        return Arguments.of(endpoint, request);
    }

    private static MockMultipartFile productPart() {
        return new MockMultipartFile(
                "product", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes(StandardCharsets.UTF_8));
    }
}
