package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Board;
import com.teamketo.shop.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

    // 목록 조회: GET /api/board/list
    @GetMapping("/list")
    public List<Board> list() {
        return boardService.getAll();
    }

    // 상세 조회: GET /api/board/detail/1
    @GetMapping("/detail/{id}")
    public Board detail(@PathVariable Long id) {
        return boardService.getDetail(id);
    }

    // 게시글 등록: POST /api/board/add
    @PostMapping("/add")
    public Board add(@RequestBody Board board) {
        return boardService.addBoard(board);
    }

    // 게시글 수정: PUT /api/board/update/1
    @PutMapping("/update/{id}")
    public Board update(@PathVariable Long id, @RequestBody Board board) {
        return boardService.updateBoard(id, board);
    }

    // 게시글 삭제: DELETE /api/board/delete/1
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.removeBoard(id);
        return "삭제 완료: " + id;
    }

}