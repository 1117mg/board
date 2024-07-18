package org.study.board.util;

import org.springframework.web.multipart.MultipartFile;
import org.study.board.dto.FileDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtil {

    public static List<FileDto> uploadFile(MultipartFile[] uploadFile) {
        List<FileDto> list = new ArrayList<>();

        for (MultipartFile file : uploadFile) {
            if (!file.isEmpty()) {
                try {
                    // UUID를 이용해 unique한 파일 이름을 생성
                    String uuid = UUID.randomUUID().toString();
                    String originalFilename = file.getOriginalFilename();
                    String uploadDir = "d:/image"; // 이미지를 저장할 디렉토리 경로

                    // 디렉토리가 없으면 생성
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // 파일 저장 경로 설정
                    Path filePath = uploadPath.resolve(uuid + "_" + originalFilename);

                    // 파일 저장
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // FileDto 생성 및 리스트에 추가
                    FileDto fileDto = new FileDto();
                    fileDto.setUuid(uuid);
                    fileDto.setFilename(originalFilename);
                    fileDto.setContentType(file.getContentType());
                    list.add(fileDto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    // 이전 버전
    /*public static List<FileDto> uploadFile(MultipartFile[] uploadFile){
        List<FileDto> list = new ArrayList<>();

        for (MultipartFile file : uploadFile) {
            if (!file.isEmpty()) {
                // UUID를 이용해 unique한 파일 이름을 만들어준다.
                FileDto dto = new FileDto();
                dto.setUuid(UUID.randomUUID().toString());
                dto.setFilename(file.getOriginalFilename());
                dto.setContentType(file.getContentType());

                File newFileName = new File(dto.getUuid() + "_" + dto.getFilename());
                try {
                    file.transferTo(newFileName);
                    list.add(dto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }*/
}
