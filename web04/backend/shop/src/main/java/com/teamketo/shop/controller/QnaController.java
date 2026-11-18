package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Qna;
import com.teamketo.shop.service.MemberService;
import com.teamketo.shop.service.QnaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaService qnaService;
    private final MemberService memberService;

    @GetMapping("/list")
    public List<Qna> list() {
        return qnaService.list();
    }

    @GetMapping("/product/{productId}")
    public List<Qna> listByProduct(@PathVariable Long productId) {
        return qnaService.listByProduct(productId);
    }

    @GetMapping("/search")
    public List<Qna> search(@RequestParam String title) {
        return qnaService.search(title);
    }

    @GetMapping("/detail/{id}")
    public List<Qna> detail(@PathVariable Long id) {
        return qnaService.detail(id);
    }

    @PostMapping("/question")
    public Qna createQuestion(@RequestBody Qna qna, Principal principal) {
        String writer = memberService.requireCurrentMember(principal.getName()).getEmail();
        return qnaService.createQuestion(qna, writer);
    }

    @PostMapping("/answer/{parentId}")
    public Qna createAnswer(@PathVariable Long parentId, @RequestBody Qna qna, Principal principal) {
        String writer = memberService.requireCurrentMember(principal.getName()).getEmail();
        return qnaService.createAnswer(parentId, qna, writer);
    }

    @PutMapping("/update/{id}")
    public Qna update(@PathVariable Long id,
                      @RequestBody Qna qna,
                      Principal principal, HttpServletRequest request) {
        String writer = memberService.requireCurrentMember(principal.getName()).getEmail();
        return qnaService.update(id, qna, writer, request.isUserInRole("ADMIN"));
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         Principal principal, HttpServletRequest request) {
        String writer = memberService.requireCurrentMember(principal.getName()).getEmail();
        qnaService.delete(id, writer, request.isUserInRole("ADMIN"));
        return "삭제 완료: " + id;
    }
}
