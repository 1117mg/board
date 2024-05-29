package org.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDto {
    private String filename;
    private String uuid;
    private String contentType;

}
