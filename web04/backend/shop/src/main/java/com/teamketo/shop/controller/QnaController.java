package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Qna;
import com.teamketo.shop.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaService qnaService;

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
    public Qna createQuestion(@RequestBody Qna qna) {
        return qnaService.createQuestion(qna);
    }

    @PostMapping("/answer/{parentId}")
    public Qna createAnswer(@PathVariable Long parentId, @RequestBody Qna qna) {
        return qnaService.createAnswer(parentId, qna);
    }

    @PutMapping("/update/{id}")
    public Qna update(@PathVariable Long id,
                      @RequestBody Qna qna,
                      @RequestParam String loginUser,
                      @RequestParam(defaultValue = "false") boolean isAdmin) {
        return qnaService.update(id, qna, loginUser, isAdmin);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam String loginUser,
                         @RequestParam(defaultValue = "false") boolean isAdmin) {
        qnaService.delete(id, loginUser, isAdmin);
        return "삭제 완료: " + id;
    }
}
