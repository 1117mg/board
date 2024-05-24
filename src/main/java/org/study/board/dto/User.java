package org.study.board.dto;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer idx;
    private String userId;
    private String password;
    private Date regdate;
}
