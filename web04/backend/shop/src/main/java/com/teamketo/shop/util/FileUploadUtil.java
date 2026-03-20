package com.teamketo.shop.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {

    //파일 업로드
    public static String saveFile(MultipartFile file, String uploadDir) throws Exception {
        File dir = new File(uploadDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        if(file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + originalName;

        // 경로 구분자 추가
        File f = new File(uploadDir + File.separator + fileName);
        file.transferTo(f);
        return fileName;
    }

    //파일 삭제
    public static void deleteFile(String fileName, String uploadPath) {
        if(fileName != null && uploadPath != null) {
            // 경로 구분자 추가
            Path path = Paths.get(uploadPath + File.separator + fileName);
            try {
                Files.delete(path);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}