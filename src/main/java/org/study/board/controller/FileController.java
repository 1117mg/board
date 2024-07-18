package org.study.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.study.board.service.BoardService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class FileController {

    @Autowired
    private BoardService boardService;

    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile upload) {
        try {
            // UUID를 이용해 unique한 파일 이름을 생성
            String uuid = UUID.randomUUID().toString();
            String originalFilename = upload.getOriginalFilename();
            String uploadDir = "d:/image"; // 이미지를 저장할 디렉토리 경로

            // 디렉토리가 없으면 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장 경로 설정
            Path filePath = uploadPath.resolve(uuid + "_" + originalFilename);

            // 파일 저장
            Files.copy(upload.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 업로드된 파일의 URL 생성
            String fileUrl = "/downloadFile/" + uuid + "_" + originalFilename;

            // JSON 응답을 위한 Map 생성
            Map<String, Object> response = new HashMap<>();
            response.put("uploaded", true);
            response.put("url", fileUrl);

            // CKEditor가 요구하는 JSON 형식으로 응답 생성
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 에러 발생 시 JSON 형식으로 에러 응답 생성
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("uploaded", false);
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", e.getMessage());
            errorResponse.put("error", errorDetails);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 파일 다운로드를 위한 엔드포인트
    @GetMapping("/downloadFile/{uuid}_{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid, @PathVariable String filename) throws IOException {
        return boardService.downloadFile(uuid, filename);
    }
}
