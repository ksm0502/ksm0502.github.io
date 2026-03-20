package com.teamketo.shop.service;

import com.teamketo.shop.entity.Board;
import com.teamketo.shop.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시판 글 목록 -> findAll => List<Board>
    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    // 게시글 상세보기 -> findById(id)
    public Board getDetail(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    // 게시글 등록 -> save(board)
    public Board addBoard(Board board) {
        return boardRepository.save(board);
    }

    //게시글 수정 : findById(id)+save(board)
    public Board updateBoard(Long id, Board board) {
        Board b = boardRepository.findById(id).orElseThrow();
        b.setTitle(board.getTitle());
        b.setContent(board.getContent());
        return boardRepository.save(b);
    }

    //게시글 삭제 : deleteById(id)
    public void removeBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
