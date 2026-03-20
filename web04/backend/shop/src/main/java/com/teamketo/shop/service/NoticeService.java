package com.teamketo.shop.service;

import com.teamketo.shop.entity.Notice;
import com.teamketo.shop.repository.NoticeRepository;
import com.teamketo.shop.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/notice";

    public List<Notice> getAll() {
        return noticeRepository.findAll();
    }

    @Transactional
    public Notice getNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        notice.incrementHits();
        return notice;
    }

    @Transactional
    public Notice addNotice(Notice notice, MultipartFile file) throws Exception {
        if(file != null && !file.isEmpty()) {
            notice.setAttachment(FileUploadUtil.saveFile(file, uploadDir));
        }
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, Notice notice, MultipartFile file) throws Exception {
        Notice noti = noticeRepository.findById(id).orElseThrow();
        if (file != null && !file.isEmpty()) {
            FileUploadUtil.deleteFile(noti.getAttachment(), uploadDir);
            noti.setAttachment(FileUploadUtil.saveFile(file, uploadDir));
        }
        noti.update(notice.getTitle(), notice.getContent(), notice.getAuthor(), noti.getAttachment());
        return noticeRepository.save(noti);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        FileUploadUtil.deleteFile(notice.getAttachment(), uploadDir);
        noticeRepository.deleteById(id);
    }
}