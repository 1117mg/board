package org.study.board.dto;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer idx;
    private String username;
    private String password;
    private Date regdate;
}
