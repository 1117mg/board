package org.study.board.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class UserBackup {
    private long idx;
    private String phoneNo;
    private Timestamp regdate;
    private String username;
    private LocalDateTime backupDate;
}
