package org.study.board.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Data
public class Board {

    private Integer bno;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String writer;
    private Date regdate;
    private Integer hit;
    private boolean deleteYn;
    private Long userIdx;
    private Integer boardType; // 0: 공지사항 게시판, 1: QnA 게시판

    //계층형 게시판
    private Integer gno;    // 그룹 번호, 원글(부모글)과 답변글은 동일한 gno를 가진다.
    private Integer sorts;  // 정렬 순서
    private Integer depth;  // 답변글이면 원글(부모글)의 depth + 1을, 원글이면 0을 가진다.
    private Integer parentBno;  // 원글(부모글)의 bno

    /*페이징 기능*/
    private Integer pageNo;	    // 현재 페이지 번호
    private int pageSize;	    // 페이지 당 항목 수
    private int pageOffset; 	// 현재 페이지 이전 항목 수

    /*첨부파일*/
    private List<FileDto> list;
    private String filename;
    private String[] uuids;
    private String[] filenames;
    private String[] contentTypes;

    // 현재 시간과 비교하여 1일 이내인지 확인하는 메소드
    public boolean isNew() {
        if (regdate == null) {
            return false;
        }
        LocalDateTime regDateTime = LocalDateTime.ofInstant(regdate.toInstant(), ZoneId.systemDefault());
        return regDateTime.isAfter(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
    }
}
