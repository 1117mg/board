package org.study.board.util;

import org.springframework.web.multipart.MultipartFile;
import org.study.board.dto.FileDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtil {
    public static List<FileDto> uploadFile(MultipartFile[] uploadFile){
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
    }
}