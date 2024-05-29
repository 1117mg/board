package org.study.board.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Board {

    private Integer bno;
    private String title;
    private String content;
    private String writer;
    private Date regdate;
    private int hit;
    private boolean deleteYn;
    private boolean noticeYn;
    private Integer userIdx;

}
