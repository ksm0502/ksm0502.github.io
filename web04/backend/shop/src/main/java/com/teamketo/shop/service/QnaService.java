package com.teamketo.shop.service;

import com.teamketo.shop.entity.Qna;
import com.teamketo.shop.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;

    // 질문 등록
    public Qna createQuestion(Qna qna) {
        qna.setLevel(1);
        Qna q = qnaRepository.save(qna);
        q.setParentId(q.getId());
        return qnaRepository.save(q);
    }

    // 답변 등록
    public Qna createAnswer(Long parentId, Qna qna) {
        qna.setLevel(2);
        qna.setParentId(parentId);
        return qnaRepository.save(qna);
    }

    // 전체 Q&A 목록 (게시판용)
    public List<Qna> list() {
        return qnaRepository.findAllGrouped();
    }

    // 특정 상품 Q&A 목록 (상품 상세 페이지용)
    public List<Qna> listByProduct(Long productId) {
        return qnaRepository.findByProductIdGrouped(productId);
    }

    // 제목으로 검색
    public List<Qna> search(String keyword) {
        return qnaRepository.searchByTitle(keyword);
    }

    // 질문/답변 상세 보기
    public List<Qna> detail(Long id) {
        Qna question = qnaRepository.findById(id).orElseThrow();
        question.setViewCount(question.getViewCount() + 1);
        qnaRepository.save(question);
        return qnaRepository.findByParentIdOrderByLevelAscCreatedAtAsc(id);
    }

    // 질문/답변 수정
    public Qna update(Long id, Qna qna, String loginUser, boolean isAdmin) {
        Qna origin = qnaRepository.findById(id).orElseThrow();
        if (!origin.getWriter().equals(loginUser) && !isAdmin) {
            throw new RuntimeException("수정 권한 없음");
        }
        origin.setTitle(qna.getTitle());
        origin.setContent(qna.getContent());
        return qnaRepository.save(origin);
    }

    // 삭제: 질문 삭제 시 답변도 삭제
    public void delete(Long id, String loginUser, boolean isAdmin) {
        Qna origin = qnaRepository.findById(id).orElseThrow();
        if (!origin.getWriter().equals(loginUser) && !isAdmin) {
            throw new RuntimeException("삭제 권한 없음");
        }
        if (origin.getLevel() == 1) {
            List<Qna> list = qnaRepository.findByParentIdOrderByLevelAscCreatedAtAsc(origin.getId());
            qnaRepository.deleteAll(list);
        } else {
            qnaRepository.deleteById(id);
        }
    }
}
