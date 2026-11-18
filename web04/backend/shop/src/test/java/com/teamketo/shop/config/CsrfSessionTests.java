package com.teamketo.shop.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.MemberRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CsrfSessionTests {

    private static final String FRONTEND_ORIGIN = "http://localhost:5173";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void csrfTokenFollowsSessionLoginAndLogout() throws Exception {
        String password = "csrf-test-password";
        Member member = memberRepository.save(Member.builder().name("CSRF test")
                .email(UUID.randomUUID() + "@example.test")
                .password(passwordEncoder.encode(password)).role(Role.USER).build());

        mockMvc.perform(post("/login").param("email", member.getEmail()).param("password", password))
                .andExpect(status().isForbidden());

        MvcResult initial = mockMvc.perform(get("/csrf").header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andReturn();
        JsonNode beforeLogin = tokenFrom(initial);
        MockHttpSession anonymous = (MockHttpSession) initial.getRequest().getSession(false);

        MvcResult login = mockMvc.perform(post("/login").session(anonymous)
                        .header("Origin", FRONTEND_ORIGIN)
                        .header(beforeLogin.get("headerName").asText(), beforeLogin.get("token").asText())
                        .param("email", member.getEmail()).param("password", password))
                .andExpect(status().isOk()).andReturn();
        MockHttpSession authenticated = (MockHttpSession) login.getRequest().getSession(false);
        mockMvc.perform(get("/member/me").session(authenticated))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(member.getEmail()));

        mockMvc.perform(post("/logout").session(authenticated))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/logout").session(authenticated)
                        .header(beforeLogin.get("headerName").asText(), beforeLogin.get("token").asText()))
                .andExpect(status().isForbidden());

        JsonNode afterLogin = tokenFrom(mockMvc.perform(get("/csrf").session(authenticated))
                .andExpect(status().isOk()).andReturn());
        mockMvc.perform(post("/logout").session(authenticated)
                        .header(afterLogin.get("headerName").asText(), afterLogin.get("token").asText()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/member/me")).andExpect(status().isUnauthorized());
    }

    private JsonNode tokenFrom(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
    }
}
