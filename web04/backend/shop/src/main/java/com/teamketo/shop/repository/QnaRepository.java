package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 전체 Q&A 목록 (게시판용)
    @Query("SELECT q FROM Qna q ORDER BY q.parentId DESC, q.level ASC, q.createdAt ASC")
    List<Qna> findAllGrouped();

    // 제목으로 검색
    @Query("SELECT q FROM Qna q WHERE q.title LIKE %:title% ORDER BY q.parentId DESC, q.level ASC, q.createdAt ASC")
    List<Qna> searchByTitle(String title);

    // 특정 질문의 답변
    List<Qna> findByParentIdOrderByLevelAscCreatedAtAsc(Long parentId);

    // 특정 상품의 Q&A 목록 (상품 상세 페이지용)
    @Query("SELECT q FROM Qna q WHERE q.productId = :productId ORDER BY q.parentId DESC, q.level ASC, q.createdAt ASC")
    List<Qna> findByProductIdGrouped(Long productId);
}
