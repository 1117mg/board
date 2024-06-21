package org.study.board.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Board {

    private Integer bno;
    private String title;
    private String content;
    private String writer;
    private Date regdate;
    private Integer hit;
    private boolean deleteYn;
    private Integer userIdx;
    private Integer boardType; // 0: 공지사항 게시판, 1: QnA 게시판

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

}
