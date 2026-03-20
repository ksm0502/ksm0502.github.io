package com.teamketo.shop.config;

import com.teamketo.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

    //인증/인가 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //모든 사용자 접근 가능
                        .requestMatchers(
                                "/", "/signup", "/login",
                                "/css/**", "/js/**", "/images/**", "/uploads/**",
                                "/member/me",               // 세션 복원용
                                "/api/products/**",         // 상품 목록/상세
                                "/api/qna/list",            // 전체 Q&A 목록
                                "/api/qna/search",          // Q&A 검색
                                "/api/qna/detail/**",       // Q&A 상세
                                "/api/qna/product/**",      // 상품별 Q&A
                                "/api/reviews/product/**",  // 상품별 리뷰
                                "/api/notice/list",         // 공지사항 목록
                                "/api/notice/detail/**"     // 공지사항 상세
                        )
                        .permitAll()
                        //일반 사용자 이상
                        .requestMatchers(
                                "/mypage/**",
                                "/api/orders/**",
                                "/api/cart/**",
                                "/api/reviews/**",
                                "/api/qna/question",
                                "/api/qna/update/**",
                                "/api/qna/delete/**"
                        )
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        //중간 관리자 이상
                        .requestMatchers("/manager/**")
                        .hasAnyRole("MANAGER", "ADMIN")
                        //최고 관리자
                        .requestMatchers("/admin/**", "/api/products/admin/**")
                        .hasRole("ADMIN")
                        //나머지는 로그인 필요
                        .anyRequest()
                        .authenticated()
                )
                //로그인 설정
                .formLogin(form ->
                        form.loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .successHandler((req, res, auth) -> {
                                    res.setStatus(200);
                                })
                                .failureHandler((req, res, e) -> {
                                    res.setStatus(401);
                                })
                )
                //로그아웃
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessHandler((req, res, auth) -> {
                                    res.setStatus(200);
                                })
                );
        return http.build();
    }

    //사용자 인증 처리
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var member = memberService.findByUsername(username);
            return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .roles(member.getRole().name())
                    .build();
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}