package com.teamketo.shop.service;

import com.teamketo.shop.entity.Qna;
import com.teamketo.shop.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;

    // 질문 등록
    @Transactional
    public Qna createQuestion(Qna qna, String writer) {
        Qna question = qnaRepository.save(newPost(qna, writer, 1, null));
        question.setParentId(question.getId());
        return question;
    }

    // 답변 등록
    public Qna createAnswer(Long parentId, Qna qna, String writer) {
        return qnaRepository.save(newPost(qna, writer, 2, parentId));
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
        Qna origin = getEditablePost(id, loginUser, isAdmin);
        origin.setTitle(qna.getTitle());
        origin.setContent(qna.getContent());
        return qnaRepository.save(origin);
    }

    // 삭제: 질문 삭제 시 답변도 삭제
    public void delete(Long id, String loginUser, boolean isAdmin) {
        Qna origin = getEditablePost(id, loginUser, isAdmin);
        if (origin.getLevel() == 1) {
            List<Qna> list = qnaRepository.findByParentIdOrderByLevelAscCreatedAtAsc(origin.getId());
            qnaRepository.deleteAll(list);
        } else {
            qnaRepository.deleteById(id);
        }
    }

    private Qna newPost(Qna content, String writer, int level, Long parentId) {
        return Qna.builder()
                .title(content.getTitle())
                .content(content.getContent())
                .productId(content.getProductId())
                .writer(writer)
                .level(level)
                .parentId(parentId)
                .build();
    }

    private Qna getEditablePost(Long id, String loginUser, boolean isAdmin) {
        Qna post = qnaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Q&A 게시글을 찾을 수 없습니다."));
        if (!isAdmin && !loginUser.equals(post.getWriter())) {
            throw new AccessDeniedException("Q&A 작성자 또는 관리자만 변경할 수 있습니다.");
        }
        return post;
    }
}
