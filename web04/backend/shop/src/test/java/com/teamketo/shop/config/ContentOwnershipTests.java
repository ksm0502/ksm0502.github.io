package com.teamketo.shop.config;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Product;
import com.teamketo.shop.entity.Qna;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.MemberRepository;
import com.teamketo.shop.repository.ProductRepository;
import com.teamketo.shop.repository.QnaRepository;
import com.teamketo.shop.repository.ReviewRepository;
import com.teamketo.shop.service.OrderService;
import com.teamketo.shop.service.QnaService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
class ContentOwnershipTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private QnaRepository qnaRepository;
    @Autowired private OrderService orderService;
    @Autowired private QnaService qnaService;

    private Member author;
    private Member otherCustomer;
    private Member admin;
    private RequestPostProcessor authorSession;
    private RequestPostProcessor otherSession;
    private RequestPostProcessor adminSession;

    @BeforeEach
    void createMembers() {
        author = createMember(Role.USER);
        otherCustomer = createMember(Role.USER);
        admin = createMember(Role.ADMIN);
        authorSession = user(author.getEmail()).roles("USER");
        otherSession = user(otherCustomer.getEmail()).roles("USER");
        adminSession = user(admin.getEmail()).roles("ADMIN");
    }

    @Test
    void reviewUsesSessionBuyerAndAllowsOnlyOwnerOrAdminToDelete() throws Exception {
        Product product = productRepository.save(Product.builder().name("Review test")
                .price(10_000).stock(10).categoryId(1L).build());
        orderService.createOrder(author.getId(), List.of(product.getId()), List.of(1),
                "QA address", "QA recipient", "010-0000-0000");
        String reviewBody = """
                {"memberId":%d,"productId":%d,"rating":5,"content":"Original review"}
                """;
        mockMvc.perform(post("/api/reviews").with(csrf()).with(otherSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewBody.formatted(author.getId(), product.getId())))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/reviews").with(csrf()).with(authorSession).contentType(MediaType.APPLICATION_JSON)
                        .content(reviewBody.formatted(otherCustomer.getId(), product.getId())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.member.id").value(author.getId().intValue()));

        Long reviewId = reviewRepository.findByMemberIdOrderByCreatedAtDesc(author.getId()).getFirst().getId();
        mockMvc.perform(get("/api/reviews/product/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Original review"))
                .andExpect(jsonPath("$[0].member.name").value(author.getName()))
                .andExpect(jsonPath("$[0].member.length()").value(1));
        mockMvc.perform(get("/api/reviews/my/{id}", author.getId()).with(otherSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reviews/my/{id}", author.getId()).with(authorSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(reviewId.intValue()));

        String update = """
                {"memberId":%d,"rating":4,"content":"Updated review"}
                """.formatted(author.getId());
        mockMvc.perform(put("/api/reviews/{id}", reviewId).with(csrf()).with(otherSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/reviews/{id}", reviewId).with(csrf()).with(otherSession)
                        .param("memberId", author.getId().toString()).param("isAdmin", "true"))
                .andExpect(status().isForbidden());
        assertThat(reviewRepository.findById(reviewId).orElseThrow().getContent()).isEqualTo("Original review");

        mockMvc.perform(put("/api/reviews/{id}", reviewId).with(csrf()).with(authorSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rating").value(4));
        mockMvc.perform(delete("/api/reviews/{id}", reviewId).with(csrf()).with(authorSession))
                .andExpect(status().isOk());
        assertThat(reviewRepository.findById(reviewId)).isEmpty();

        mockMvc.perform(post("/api/reviews").with(csrf()).with(authorSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewBody.formatted(author.getId(), product.getId())))
                .andExpect(status().isOk());
        Long moderatedId = reviewRepository.findByMemberIdOrderByCreatedAtDesc(author.getId()).getFirst().getId();
        mockMvc.perform(delete("/api/reviews/{id}", moderatedId).with(csrf()).with(adminSession))
                .andExpect(status().isOk());
        assertThat(reviewRepository.findById(moderatedId)).isEmpty();
    }

    @Test
    void adminMemberResponsesExcludePasswordHashes() throws Exception {
        createMember(Role.MANAGER);
        for (String endpoint : List.of("/admin/members", "/admin/users", "/admin/managers")) {
            mockMvc.perform(get(endpoint).with(adminSession))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email").isString())
                    .andExpect(jsonPath("$..password").isEmpty());
        }
        mockMvc.perform(get("/admin/member/{email}", author.getEmail()).with(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(author.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void qnaUsesSessionWriterAndPreservesAuthorAndAdminActions() throws Exception {
        Qna question = qnaService.createQuestion(Qna.builder().title("Original question").content("Question").build(),
                author.getEmail());
        mockMvc.perform(post("/api/qna/question").with(csrf()).with(otherSession).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"title":"New question","content":"New content","writer":"%s"}
                                """.formatted(question.getId(), author.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(not(question.getId().intValue())))
                .andExpect(jsonPath("$.writer").value(otherCustomer.getEmail()));

        String update = "{\"title\":\"Updated question\",\"content\":\"Edited content\"}";
        mockMvc.perform(put("/api/qna/update/{id}", question.getId()).with(csrf()).with(otherSession)
                        .param("loginUser", author.getEmail()).param("isAdmin", "true")
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/qna/delete/{id}", question.getId()).with(csrf()).with(otherSession)
                        .param("loginUser", author.getEmail()).param("isAdmin", "true"))
                .andExpect(status().isForbidden());
        assertThat(qnaRepository.findById(question.getId()).orElseThrow().getTitle()).isEqualTo("Original question");
        mockMvc.perform(put("/api/qna/update/{id}", question.getId()).with(csrf()).with(authorSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated question"));

        String answer = """
                {"title":"Answer","content":"Answer content","writer":"%s"}
                """.formatted(author.getEmail());
        mockMvc.perform(post("/api/qna/answer/{id}", question.getId()).with(csrf()).with(otherSession)
                        .contentType(MediaType.APPLICATION_JSON).content(answer))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/qna/answer/{id}", question.getId()).with(csrf()).with(adminSession)
                        .contentType(MediaType.APPLICATION_JSON).content(answer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.writer").value(admin.getEmail()));
        mockMvc.perform(get("/api/qna/detail/{id}", question.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$[1].title").value("Answer"));
        mockMvc.perform(put("/api/qna/update/{id}", question.getId()).with(csrf()).with(adminSession)
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/qna/delete/{id}", question.getId()).with(csrf()).with(adminSession))
                .andExpect(status().isOk());
        assertThat(qnaRepository.findByParentIdOrderByLevelAscCreatedAtAsc(question.getId())).isEmpty();
    }

    private Member createMember(Role role) {
        return memberRepository.save(Member.builder().name(role.name())
                .email(UUID.randomUUID() + "@example.test")
                .password("unused-by-mock-session").role(role).build());
    }
}
