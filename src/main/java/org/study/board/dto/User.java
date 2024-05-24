package org.study.board.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {

    private Long idx;
    private String userId;
    private String password;
    private String username;
    private Timestamp regdate;
}
