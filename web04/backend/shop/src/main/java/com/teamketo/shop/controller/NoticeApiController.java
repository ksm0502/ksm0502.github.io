package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Notice;
import com.teamketo.shop.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public List<Notice> getAllNotices() {
        return noticeService.getAll();
    }

    @GetMapping("/detail/{id}")
    public Notice getNotice(@PathVariable("id") Long id) {
        return noticeService.getNotice(id);
    }

    @PostMapping("/save")
    public Notice createNotice(@ModelAttribute Notice notice,
                               @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        return noticeService.addNotice(notice, file);
    }

    @PutMapping("/update/{id}")
    public Notice updateNotice(@PathVariable("id") Long id,
                               @ModelAttribute Notice notice,
                               @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        return noticeService.updateNotice(id, notice, file);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNotice(@PathVariable("id") Long id) {
        noticeService.deleteNotice(id);
    }
}
